package osmo.tester.generator;

import osmo.common.log.Logger;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.filter.TransitionFilter;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.dataflow.ScriptedValueProvider;

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
  /** Test generation history. */
  private TestSuite suite;
  /** The set of enabled transitions in the current state is passed to this algorithm to pick one to execute. */
  private FSMTraversalAlgorithm algorithm;
  /** Defines when test suite generation should be stopped. Invoked between each test case. */
  private Collection<EndCondition> suiteEndConditions;
  /** Defines when test case generation should be stopped. Invoked between each test step. */
  private Collection<EndCondition> testCaseEndConditions;
  /** Defines which transitions should be filtered out. Invoked before any transition is taken. */
  private Collection<TransitionFilter> filters;
  /** The list of listeners to be notified of new events as generation progresses. */
  private GenerationListenerList listeners;
  /** This is set when the test should end but @EndState is not yet achieved to signal ending ASAP. */
  private boolean testEnding = false;
  /** If true, test generation will fail when no enabled transitions are found. If false, test ends. */
  private boolean failWhenNoWayForward = true;
  /** The parsed overall test model. */
  private final FSM fsm;
  /** Keeps track of overall number of tests generated. */
  private static int testCount = 0;
  /** Provides variable values if running a manually defined script. */
  private ScriptedValueProvider scripter = null;

  /**
   * Constructor.
   *
   * @param fsm Describes the test model in an FSM format.
   */
  public MainGenerator(FSM fsm) {
    this.fsm = fsm;
  }

  /** @param algorithm The set of enabled transitions in the current state is passed to this algorithm to pick one to execute. */
  public void setAlgorithm(FSMTraversalAlgorithm algorithm) {
    this.algorithm = algorithm;
  }

  /** @param suiteEndConditions Defines when test suite generation should be stopped. Invoked between each test case. */
  public void setSuiteEndConditions(Collection<EndCondition> suiteEndConditions) {
    this.suiteEndConditions = suiteEndConditions;
  }

  /** @param testCaseEndConditions Defines when test case generation should be stopped. Invoked between each test step. */
  public void setTestCaseEndConditions(Collection<EndCondition> testCaseEndConditions) {
    this.testCaseEndConditions = testCaseEndConditions;
  }

  /** @param filters The new filters to define which transitions should not be taken at a given time. */
  public void setFilters(Collection<TransitionFilter> filters) {
    this.filters = filters;
  }

  /** @param listeners Listeners to be notified about generation events. */
  public void setListeners(GenerationListenerList listeners) {
    this.listeners = listeners;
  }

  public void setFailWhenNoWayForward(boolean failWhenNoWayForward) {
    this.failWhenNoWayForward = failWhenNoWayForward;
  }

  /** Invoked to start the test generation using the configured parameters. */
  public void generate() {
    initSuite();
    while (!checkSuiteEndConditions() && !suite.shouldEndSuite()) {
      suite.setShouldEndTest(false);
      next();
    }
    endSuite();
  }

  /** Initializes test generation. Should be called before any tests are generated. */
  public void initSuite() {
    initEndConditions();
    suite = fsm.initSuite(scripter);
    log.debug("Starting test suite generation");
    beforeSuite();
    listeners.init(fsm);
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
  public TestCase next() {
    testCount++;
    log.debug("Starting new test generation");
    beforeTest();
    TestCase test = suite.getCurrentTest();
    while (!checkTestCaseEndConditions()) {
      List<FSMTransition> enabled = getEnabled();
      if (enabled.size() == 0) {
        if (failWhenNoWayForward) {
          throw new IllegalStateException("No transition available.");
        } else {
          log.debug("No enabled transitions, ending test (fail is disabled).");
          break;
        }
      }
      FSMTransition next = algorithm.choose(suite, enabled);
      if (suite.shouldEndTest()) {
        break;
      }
      log.debug("Taking transition " + next.getName());
      execute(fsm, next);
      if (checkModelEndConditions()) {
        //stop this test case generation if any end condition returns true
        break;
      }
    }
    afterTest();
    log.debug("Finished new test generation");
    return test;
  }

  private void initEndConditions() {
    for (EndCondition ec : testCaseEndConditions) {
      ec.init(fsm);
    }
    for (EndCondition ec : suiteEndConditions) {
      ec.init(fsm);
    }
  }

  /**
   * Checks if suite generation should stop.
   *
   * @return True if should stop.
   */
  private boolean checkSuiteEndConditions() {
    boolean shouldEnd = true;
    for (EndCondition ec : suiteEndConditions) {
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
    for (EndCondition ec : testCaseEndConditions) {
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
    //update history
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
    List<FSMTransition> enabled = new ArrayList<FSMTransition>();
    enabled.addAll(allTransitions);
    //filter out all non-wanted transitions
    for (TransitionFilter filter : filters) {
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

  public void setValueScripter(ScriptedValueProvider scripter) {
    this.scripter = scripter;
  }
}
