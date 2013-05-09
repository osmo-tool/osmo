package osmo.tester.generator;

import osmo.common.OSMOException;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.filter.TransitionFilter;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.Requirements;
import osmo.tester.parser.MainParser;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * The main test generator class.
 * Takes as input the finite state machine model parsed by {@link osmo.tester.parser.MainParser}.
 * Runs test generation on this model using the defined algorithms, exit strategies, etc.
 *
 * @author Teemu Kanstren
 */
public class MainGenerator {
  private static Logger log = new Logger(MainGenerator.class);
  /** The test generation configuration. */
  protected final OSMOConfiguration config;
  /** The parsed overall test model. */
  private FSM fsm;
  /** The list of listeners to be notified of new events as generation progresses. */
  private GenerationListenerList listeners;
  /** Test generation history. */
  private TestSuite suite;
  /** Requirements for model objects. */
  private Requirements reqs;
  /** Keeps track of overall number of tests generated. */
  private static int testCount = 0;
  /** Set of possible pairs, key = source, value = targets. */
  private Collection<String> possiblePairs = new HashSet<>();

  /**
   * Constructor.
   *
   * @param config The configuration for test generation parameters.
   */
  public MainGenerator(TestSuite suite, OSMOConfiguration config) {
    this.suite = suite;
    this.config = config;
    this.listeners = config.getListeners();
    createModelObjects();
  }

  /** Invoked to start the test generation using the configured parameters. */
  public void generate() {
    log.debug("starting generation");
    initSuite();
    while (!config.getSuiteEndCondition().endSuite(suite, fsm)) {
      nextTest();
    }
    log.debug("Ending suite");
    endSuite();
  }

  private void createModelObjects() {
    if (config.getFactory() == null && fsm != null) return;
    if (config.getFactory() != null) {
      long baseSeed = OSMOConfiguration.getBaseSeed();
      int salt = suite.getCoverage().getTransitions().size();
      OSMOConfiguration.setSeed(baseSeed + salt);
    }
    MainParser parser = new MainParser();
    ParserResult result = parser.parse(config, suite);
    config.check(result);
    fsm = result.getFsm();
    fsm.initSearchableInputs(config);
    invokeAll(fsm.getGenerationEnablers());
    this.reqs = result.getRequirements();
    if (reqs != null) {
      reqs.setTestSuite(suite);
    }
  }

  /**
   * Generates a new test case from the test model.
   *
   * @return The generated test case.
   */
  public TestCase nextTest() {
    createModelObjects();
    FSMTraversalAlgorithm algorithm = config.getAlgorithm();
    algorithm.initTest();
    log.debug("Starting new test generation");
    beforeTest();
    TestCase test = suite.getCurrentTest();
    while (!config.getTestCaseEndCondition().endTest(suite, fsm)) {
      try {
        boolean shouldContinue = nextStep();
        if (!shouldContinue) {
          log.debug("Ending test case");
          break;
        }
      } catch (RuntimeException | AssertionError e) {
        test.setFailed(true);
        TestCaseStep step = suite.getCurrentTest().getCurrentStep();
        if (step != null) {
          test.getCurrentStep().setFailed(true);
        }
        Throwable unwrap = unwrap(e);
        String errorMsg = "Error in test generation:"+unwrap.getMessage();
        log.error(errorMsg, e);
        listeners.testError(test, unwrap);
        if (!(unwrap instanceof AssertionError) || config.shouldFailWhenError()) {
          if (step != null) {
            step.storeGeneralState(fsm);
          }
          afterTest();
          afterSuite();
          if (unwrap instanceof RuntimeException) {
            throw (RuntimeException) unwrap;
          }
          throw new OSMOException(errorMsg, unwrap);
        } else {
          unwrap.printStackTrace();
        }
        log.debug("Skipped test error due to settings (no fail when error)", e);
      }
    }
    afterTest();
    log.debug("Finished new test generation");
    return test;
  }

  private boolean nextStep() {
    List<FSMTransition> enabled = getEnabled();
    addOptionsFor(suite.getCurrentTest().getCurrentStep(), enabled);
    if (enabled.size() == 0) {
      if (config.shouldFailWhenNoWayForward()) {
        throw new IllegalStateException("No transition available.");
      } else {
        log.debug("No enabled transitions, ending test (fail is disabled).");
        return false;
      }
    }
    FSMTraversalAlgorithm algorithm = config.getAlgorithm();
    FSMTransition next = algorithm.choose(suite, enabled);
    log.debug("Taking transition " + next.getName());
    execute(next);
    if (checkModelEndConditions()) {
      //stop this test case generation if any end condition returns true
      return false;
    }
    return true;
  }

  /**
   * Executes the given transition on the given model.
   *
   * @param transition The transition to be executed.
   */
  public void execute(FSMTransition transition) {
    transition.reset();
    //we have to add this first or it will produce failures..
    TestCaseStep step = suite.addStep(transition);
    //store state variable values for pre-methods
    transition.updatePrePostMap(fsm);
    //store into test step the current state
    step.start();
    invokeAll(transition.getPreMethods(), transition.getPrePostParameter(), "pre", transition);
    //this is the actual transition/test step being executed
    InvocationTarget target = transition.getTransition();
    if (transition.isStrict()) {
      target.invoke();
    } else {
      try {
        target.invoke();
      } catch (Exception e) {
        e.printStackTrace();
        listeners.testError(getCurrentTest(), e);
      }
    }
    //we store the "custom" state returned by @StateName tagged methods
    //we do it here to allow any post-processing of state value for a step
    storeUserState(step);
    //re-store state into transition to update parameters for post-methods
    transition.updatePrePostMap(fsm);
    //store into test step the current state, do it here to allow the post methods and listeners to see step state
    step.storeGeneralState(fsm);
    listeners.step(step);
    invokeAll(transition.getPostMethods(), transition.getPrePostParameter(), "post", transition);
    //set end time
    step.end();
  }

  /**
   * Updates the state in the test suite and stores it into test step etc.
   *
   * @param step The step.
   */
  private void storeUserState(TestCaseStep step) {
    InvocationTarget stateName = fsm.getStateNameFor(step.getModelObjectName());
    if (stateName != null) {
      String state = (String) stateName.invoke(step);
      if (state == null) {
        throw new NullPointerException("Model state is null. Now allowed.");
      }
      step.setUserState(state);
      log.debug("new state:" + state);
    }

  }

  /** Handles suite shutdown. Should be called after all tests have been generated. */
  public void endSuite() {
    afterSuite();
    log.debug("Finished test suite generation");
  }

  /**
   * Invokes the given set of methods on the target test object.
   *
   * @param targets The methods to be invoked.
   */
  public void invokeAll(Collection<InvocationTarget> targets) {
    for (InvocationTarget target : targets) {
      target.invoke();
    }
  }

  /**
   * Invokes the given set of methods on the target test object.
   *
   * @param targets    The methods to be invoked.
   * @param arg        Argument to methods invoked.
   * @param element    Type of model element (pre or post)
   * @param transition Transition to which the invocations are related.
   */
  protected void invokeAll(Collection<InvocationTarget> targets, Object arg, String element, FSMTransition transition) {
    for (InvocationTarget target : targets) {
      if (element.equals("pre")) {
        listeners.pre(transition);
      }
      if (element.equals("post")) {
        listeners.post(transition);
      }
      target.invoke(arg);
    }
  }

  /**
   * Calls every defined end condition and if any return true, also returns true. Otherwise, false.
   *
   * @return true if current test case (not suite) generation should be stopped.
   */
  protected boolean checkModelEndConditions() {
    Collection<InvocationTarget> endConditions = fsm.getEndConditions();
    for (InvocationTarget ec : endConditions) {
      Boolean result = (Boolean) ec.invoke();
      if (result) {
        log.debug("model @EndCondition signalled to stop");
        return true;
      }
    }
    return false;
  }

  public void initSuite() {
    if (suite == null) {
      log.debug("No suite object defined. Creating new.");
      //user the setSuite method to also initialize the suite object missing state
      suite = new TestSuite();
    }
    suite.reset();
    suite.initRequirements(reqs);
    //re-get since suite might have initialized it if it was null
    reqs = suite.getRequirements();
    fsm.setRequirements(reqs);
    listeners.suiteStarted(suite);
    Collection<InvocationTarget> befores = fsm.getBeforeSuites();
    invokeAll(befores);
  }

  protected void afterSuite() {
    Collection<InvocationTarget> afters = fsm.getAfterSuites();
    invokeAll(afters);
    listeners.suiteEnded(suite);
  }

  public TestCase beforeTest() {
    testCount++;
    //re-initialize end conditions before new tests to remove previous test state
    config.getTestCaseEndCondition().init(fsm);
    //update suite
    TestCase test = suite.startTest();
    listeners.testStarted(suite.getCurrentTest());
    Collection<InvocationTarget> befores = fsm.getBefores();
    invokeAll(befores);
    return test;
  }

  public void afterTest() {
    Collection<InvocationTarget> afters = fsm.getAfters();
    invokeAll(afters);
    TestCase current = suite.getCurrentTest();
    //update history
    suite.endTest();
    listeners.testEnded(current);
  }

  /** Resets the suite, removing references to any generated tests. */
  public void resetSuite() {
    suite.reset();
  }

  /**
   * Goes through all {@link osmo.tester.annotation.Transition} tagged methods in the given test model object,
   * invokes all associated {@link osmo.tester.annotation.Guard} tagged methods matching those transitions,
   * returning the set of {@link osmo.tester.annotation.Transition} methods that have no guards returning a value
   * of {@code false}.
   *
   * @return The list of enabled {@link osmo.tester.annotation.Transition} methods.
   */
  public List<FSMTransition> getEnabled() {
    List<FSMTransition> enabled = new ArrayList<>();
    enabled.addAll(fsm.getTransitions());
    //filter out all non-wanted transitions
    for (TransitionFilter filter : config.getFilters()) {
      filter.filter(enabled);
    }
    //sort the remaining ones to get deterministic test generation
    Collections.sort(enabled);
    //then check which of the remaining are allowed by their guard statements
    for (Iterator<FSMTransition> i = enabled.iterator() ; i.hasNext() ; ) {
      FSMTransition transition = i.next();
      for (InvocationTarget guard : transition.getGuards()) {
        listeners.guard(transition);
        Boolean result = (Boolean) guard.invoke();
        if (!result) {
          i.remove();
          break;
        }
      }
    }
    return enabled;
  }

  protected Throwable unwrap(Throwable e) {
    while (e instanceof OSMOException || e instanceof InvocationTargetException) {
      if (e.getCause() == null) {
        //might happen if OSMO throws OSMOException
        break;
      }
      e = e.getCause();
    }
//    Throwable cause = e.getCause();
//    if (cause != null) {
//      if (e.getClass().equals(OSMOException.class)) {
//        if (cause.getClass().equals(InvocationTargetException.class)) {
//          cause = cause.getCause();
//          if (cause.getClass().equals(RuntimeException.class)) {
//            return cause;
//          }
//          //hack to avoid Java reflection + compiler checking incompatibilities
//          return new OSMOException(cause);
//        }
//      }
//    }
    return e;
  }

  /** @return Number of tests generated by this generator. Not affected by suite reset. */
  public int getTestCount() {
    return testCount;
  }

  public TestSuite getSuite() {
    return suite;
  }

  public TestCase getCurrentTest() {
    return suite.getCurrentTest();
  }

  public FSM getFsm() {
    return fsm;
  }

  public void addOptionsFor(TestCaseStep step, List<FSMTransition> enabled) {
    String stepName = FSM.START_NAME;
    if (step != null) {
      stepName = step.getName();
    }
    for (FSMTransition transition : enabled) {
      possiblePairs.add(stepName + "->" + transition.getStringName());
    }
  }

  public Collection<String> getPossiblePairs() {
    return possiblePairs;
  }
}
