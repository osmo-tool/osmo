package osmo.tester.generator;

import osmo.common.OSMOException;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.filter.TransitionFilter;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
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
  private final OSMOConfiguration config;
  /** Test generation history. */
  private TestSuite suite;
  /** The list of listeners to be notified of new events as generation progresses. */
  private GenerationListenerList listeners;
  /** This is set when the test should end but @EndState is not yet achieved to signal ending ASAP. */
  private boolean testEnding = false;
  /** The parsed overall test model. */
  private final FSM fsm;
  /** Keeps track of overall number of tests generated. */
  private static int testCount = 0;

  /**
   * Constructor.
   *
   * @param fsm    Describes the test model in an FSM format.
   * @param config The configuration for test generation parameters.
   */
  public MainGenerator(FSM fsm, OSMOConfiguration config) {
    this.fsm = fsm;
    this.config = config;
    this.listeners = config.getListeners();
  }

  /** Invoked to start the test generation using the configured parameters. */
  public void generate() {
    initSuite();
    while (!checkSuiteEndConditions() && !suite.shouldEndSuite()) {
      suite.setShouldEndTest(false);
      nextTest();
    }
    endSuite();
  }

  /** Initializes test generation. Should be called before any tests are generated. */
  public void initSuite() {
    suite = fsm.getSuite();
    log.debug("Starting test suite generation");
    beforeSuite();
  }

  /** Handles suite shutdown. Should be called after all tests have been generated. */
  public void endSuite() {
    afterSuite();
    log.debug("Finished test suite generation");
  }

  /**
   * Generates a new test case from the test model.
   *
   * @return The generated test case.
   */
  public TestCase nextTest() {
    for (EndCondition ec : config.getTestCaseEndConditions()) {
      //re-initialize end conditions before new tests to remove previous test state
      ec.init(fsm);
    }
    testCount++;
    log.debug("Starting new test generation");
    beforeTest();
    fsm.initSearchableInputs(config);
    TestCase test = suite.getCurrentTest();
    try {
      while (!checkTestCaseEndConditions()) {
        boolean stepOk = nextStep();
        if (!stepOk) {
          break;
        }
      }
    } catch (RuntimeException e) {
      log.error("Error in test generation", e);
      if (config.shouldUnwrapExceptions()) {
        e = unwrap(e);
      }
      if (config.shouldFailWhenError()) {
        listeners.testError(test, e);
        TestStep step = suite.getCurrentTest().getCurrentStep();
        if (step != null) {
          step.storeStateAfter(fsm);
        }
        afterTest();
        afterSuite();
        throw e;
      } else {
        listeners.testError(test, unwrap(e));
        e.printStackTrace();
      }
      log.debug("Skipped test error due to settings (no fail when error)");
    }
    afterTest();
    log.debug("Finished new test generation");
    return test;
  }

  private RuntimeException unwrap(RuntimeException e) {
    Throwable cause = e.getCause();
    if (cause != null) {
      if (e.getClass().equals(OSMOException.class)) {
        if (cause.getClass().equals(InvocationTargetException.class)) {
          cause = cause.getCause();
          if (cause.getClass().equals(RuntimeException.class)) {
            return (RuntimeException) cause;
          }
          //hack to avoid Java reflection + compiler checking incompatibilities
          return new RuntimeException(cause);
        }
      }
    }
    return e;
  }

  private boolean nextStep() {
    List<FSMTransition> enabled = getEnabled();
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
    if (suite.shouldEndTest()) {
      return false;
    }
    log.debug("Taking transition " + next.getName());
    execute(fsm, next);
    if (checkModelEndConditions()) {
      //stop this test case generation if any end condition returns true
      return false;
    }
    return true;
  }

  /**
   * Checks if suite generation should stop.
   *
   * @return True if should stop.
   */
  private boolean checkSuiteEndConditions() {
    boolean shouldEnd = true;
    for (EndCondition ec : config.getSuiteEndConditions()) {
      boolean temp = ec.endSuite(suite, fsm);
      if (ec.isStrict() && temp) {
        return true;
      }
      if (!temp) {
        shouldEnd = false;
      }
    }
    return shouldEnd;
  }

  /**
   * Check if generation of current test case should stop based on given end conditions.
   *
   * @return True if this test generation should stop.
   */
  private boolean checkTestCaseEndConditions() {
    if (testEnding) {
      //allow ending only if end state annotations are not present or return true
      return checkEndStates();
    }
    boolean shouldEnd = true;
    for (EndCondition ec : config.getTestCaseEndConditions()) {
      //check if all end conditions are met
      boolean temp = ec.endTest(suite, fsm);
      if (ec.isStrict() && temp) {
        return true;
      }
      if (!temp) {
        shouldEnd = false;
      }
    }
    if (!shouldEnd) {
      return false;
    }
    testEnding = true;
    if (fsm.getEndStates().size() > 0) {
      return checkEndStates();
    }
    return true;
  }

  /** @return True if there are @EndState defined and they are allowed to stop. */
  private boolean checkEndStates() {
    Collection<InvocationTarget> endStates = fsm.getEndStates();
    for (InvocationTarget es : endStates) {
      Boolean endable = (Boolean) es.invoke();
      if (endable) {
        return true;
      }
    }
    return false;
  }

  /**
   * Calls every defind end condition and if any return true, also returns true. Otherwise, false.
   *
   * @return true if current test case (not suite) generation should be stopped.
   */
  private boolean checkModelEndConditions() {
    Collection<InvocationTarget> endConditions = fsm.getEndConditions();
    for (InvocationTarget ec : endConditions) {
      Boolean result = (Boolean) ec.invoke();
      if (result) {
        return true;
      }
    }
    return false;
  }

  private void beforeSuite() {
    listeners.suiteStarted(suite);
    Collection<InvocationTarget> befores = fsm.getBeforeSuites();
    invokeAll(befores);
  }

  private void afterSuite() {
    Collection<InvocationTarget> afters = fsm.getAfterSuites();
    invokeAll(afters);
    listeners.suiteEnded(suite);
  }

  private void beforeTest() {
    //update suite
    suite.startTest();
    listeners.testStarted(suite.getCurrentTest());
    Collection<InvocationTarget> befores = fsm.getBefores();
    invokeAll(befores);
  }

  private void afterTest() {
    Collection<InvocationTarget> afters = fsm.getAfters();
    invokeAll(afters);
    TestCase current = suite.getCurrentTest();
    //update history
    suite.endTest();
    listeners.testEnded(current);
    testEnding = false;
  }

  /**
   * Invokes the given set of methods on the target test object.
   *
   * @param targets The methods to be invoked.
   */
  private void invokeAll(Collection<InvocationTarget> targets) {
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
  private void invokeAll(Collection<InvocationTarget> targets, Object arg, String element, FSMTransition transition) {
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
   * Goes through all {@link osmo.tester.annotation.Transition} tagged methods in the given test model object,
   * invokes all associated {@link osmo.tester.annotation.Guard} tagged methods matching those transitions,
   * returning the set of {@link osmo.tester.annotation.Transition} methods that have no guards returning a value
   * of {@code false}.
   *
   * @return The list of enabled {@link osmo.tester.annotation.Transition} methods.
   */
  private List<FSMTransition> getEnabled() {
    Collection<FSMTransition> allTransitions = fsm.getTransitions();
    List<FSMTransition> enabled = new ArrayList<>();
    enabled.addAll(allTransitions);
    //filter out all non-wanted transitions
    for (TransitionFilter filter : config.getFilters()) {
      filter.filter(enabled);
    }
    //then check which of the remaining are allowed by their guard statements
    for (FSMTransition transition : allTransitions) {
      for (InvocationTarget guard : transition.getGuards()) {
        listeners.guard(transition);
        Boolean result = (Boolean) guard.invoke();
        if (!result) {
          enabled.remove(transition);
        }
      }
    }
    return enabled;
  }

  /**
   * Executes the given transition on the given model.
   *
   * @param fsm        The FSM model to which the transition belongs.
   * @param transition The transition to be executed.
   */
  public void execute(FSM fsm, FSMTransition transition) {
    transition.reset();
    //we have to add this first or it will produce failures..
    TestStep step = suite.addStep(transition);
    //store state variable values for pre-methods
    transition.storeState(fsm);
    //store into test step the current state
    step.storeStateBefore(fsm);
    invokeAll(transition.getPreMethods(), transition.getPrePostParameter(), "pre", transition);
    listeners.transition(transition);
    InvocationTarget target = transition.getTransition();
    target.invoke();
    //store into test step the current state
    step.storeStateAfter(fsm);
    //re-store state into transition to update parameters for post-methods
    transition.storeState(fsm);
    invokeAll(transition.getPostMethods(), transition.getPrePostParameter(), "post", transition);
  }
  
  /** Resets the suite, removing references to any generated tests. */
  public void resetSuite() {
    suite.reset();
  }

  /** @return Number of tests generated by this generator. Not affected by suite reset. */
  public int getTestCount() {
    return testCount;
  }
}
