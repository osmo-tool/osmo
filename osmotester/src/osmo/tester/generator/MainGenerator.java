package osmo.tester.generator;

import osmo.common.OSMOException;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.filter.StepFilter;
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
  /** The base seed to use for test generation. Each new test case gets a different seed based on this. */
  private final long baseSeed;
  /** The seed for the current test case being generated. */
  private Long seed = null;

  /**
   * @param seed   The base seed to use for randomization during generation.
   * @param suite  This is where the generated tests are stored.
   * @param config The configuration for test generation parameters.
   */
  public MainGenerator(long seed, TestSuite suite, OSMOConfiguration config) {
    this.baseSeed = seed;
    this.seed = baseSeed;
    this.suite = suite;
    this.config = config;
    this.listeners = config.getListeners();
    createModelObjects();
  }

  /** Invoked to start the test generation using the configured parameters. */
  public void generate() {
    log.debug("starting generation");
    config.initializeGeneratorElements(seed, fsm);
    initSuite();
    while (!config.getSuiteEndCondition().endSuite(suite, fsm)) {
      nextTest();
    }
    log.debug("Ending suite");
    endSuite();
  }

  /**
   * Creates all the model object instances as required. If a factory is defined, that is called.
   * If a set of classes is given, a special factory is used to create objects using reflection.
   * If no factory and no classes is given, it is assumed a set of object instances has been provided
   * by the user and these will be re-used across the generation.
   */
  private void createModelObjects() {
    if (config.getFactory() == null && fsm != null) return;
    if (config.getFactory() != null) {
      int salt = suite.getCoverage().getSteps().size();
      //create a new seed for the new test case
      seed = baseSeed + salt;
    }
    //re-parse the model, which causes re-creation of the model objects and as such creates the new references
    //to the new object instances. invocationtargets for guards, steps, etc. need updating and this is needed for that.
    MainParser parser = new MainParser();
    ParserResult result = parser.parse(seed, config, suite);
    fsm = result.getFsm();
    invokeAll(fsm.getGenerationEnablers());
    this.reqs = result.getRequirements();
    suite.initRequirements(reqs);
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

  /**
   * Take the next step in the current test case.
   * 
   * @return True if this test should end now.
   */
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
   * Executes the given transition on the given model as a test step.
   *
   * @param transition The transition to be executed.
   */
  public void execute(FSMTransition transition) {
    //we have to add this first or it will produce failures..
    TestCaseStep step = suite.addStep(transition);
    //store into test step the current state
    step.start();
    invokeAll(transition.getPreMethods(), "pre", transition);
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
    //store into test step the current state, do it here to allow the post methods and listeners to see step state
    step.storeGeneralState(fsm);
    listeners.step(step);
    invokeAll(transition.getPostMethods(), "post", transition);
    //set end time
    step.end();
  }

  /**
   * Updates the user defined state (@StateName) in the test suite and stores it into test step etc.
   *
   * @param step The step to store data into.
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
   * @param element    Type of model element (pre or post)
   * @param transition Transition to which the invocations are related.
   */
  protected void invokeAll(Collection<InvocationTarget> targets, String element, FSMTransition transition) {
    for (InvocationTarget target : targets) {
      if (element.equals("pre")) {
        listeners.pre(transition);
      }
      if (element.equals("post")) {
        listeners.post(transition);
      }
      target.invoke();
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

  /**
   * Initializes the test suite with valid values and invokes @BeforeSuite before test generation is started.
   */
  public void initSuite() {
    if (suite == null) {
      log.debug("No suite object defined. Creating new.");
      //use the setSuite method to also initialize the suite object missing state
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

  /**
   * For @AfterSuite annotations.
   */
  protected void afterSuite() {
    Collection<InvocationTarget> afters = fsm.getAfterSuites();
    invokeAll(afters);
    listeners.suiteEnded(suite);
  }

  /**
   * For @BeforeTest annotations.
   * 
   * @return The new test case.
   */
  public TestCase beforeTest() {
    testCount++;
    //re-initialize end conditions before new tests to remove previous test state
    config.getTestCaseEndCondition().init(seed, fsm);
    //update suite
    TestCase test = suite.startTest();
    listeners.testStarted(suite.getCurrentTest());
    Collection<InvocationTarget> befores = fsm.getBeforeTests();
    invokeAll(befores);
    return test;
  }

  /**
   * For @AfterTest annotations.
   */
  public void afterTest() {
    Collection<InvocationTarget> afters = fsm.getAfterTests();
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
   * Goes through all {@link osmo.tester.annotation.TestStep} tagged methods in the given test model object,
   * invokes all associated {@link osmo.tester.annotation.Guard} tagged methods matching those test steps,
   * returning the set of {@link osmo.tester.annotation.TestStep} methods that have no guards returning a value
   * of {@code false}.
   *
   * @return The list of enabled {@link osmo.tester.annotation.Transition} methods.
   */
  public List<FSMTransition> getEnabled() {
    List<FSMTransition> enabled = new ArrayList<>();
    enabled.addAll(fsm.getTransitions());
    //filter out all non-wanted transitions
    for (StepFilter filter : config.getFilters()) {
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

  /**
   * Helps keep track of possible pairs of test steps encountered during test generation.
   * This can be used in reporting to list what was covered and what was not.
   * 
   * @param step    The current executed step.
   * @param enabled The set of enabled steps in this state (next steps from current "step").
   */
  public void addOptionsFor(TestCaseStep step, List<FSMTransition> enabled) {
    String stepName = FSM.START_STEP_NAME;
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
