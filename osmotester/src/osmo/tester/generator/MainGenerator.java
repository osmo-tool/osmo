package osmo.tester.generator;

import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;

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
  /** Test generation history. Initialized from the given model to enable sharing the object with model and generator. */
  private TestSuite suite = null;
  /** The set of enabled transitions in the current state is passed to this algorithm to pick one to execute. */
  private FSMTraversalAlgorithm algorithm;
  /** Defines when test suite generation should be stopped. Invoked between each test case. */
  private Collection<EndCondition> suiteEndConditions;
  /** Defines when test case generation should be stopped. Invoked between each test step. */
  private Collection<EndCondition> testCaseEndConditions;
  /** The list of listeners to be notified of new events as generation progresses. */
  private GenerationListenerList listeners;
  /** This is set when the test should end but @EndState is not yet achieved to signal ending ASAP. */
  private boolean testEnding = false;

  /**
   * Constructor.
   */
  public MainGenerator() {
  }

  /**
   *
   * @param algorithm The set of enabled transitions in the current state is passed to this algorithm to pick one to execute.
   */
  public void setAlgorithm(FSMTraversalAlgorithm algorithm) {
    this.algorithm = algorithm;
  }

  /**
   *
   * @param suiteEndConditions Defines when test suite generation should be stopped. Invoked between each test case.
   */
  public void setSuiteEndConditions(Collection<EndCondition> suiteEndConditions) {
    this.suiteEndConditions = suiteEndConditions;
  }

  /**
   *
   * @param testCaseEndConditions Defines when test case generation should be stopped. Invoked between each test step.
   */
  public void setTestCaseEndConditions(Collection<EndCondition> testCaseEndConditions) {
    this.testCaseEndConditions = testCaseEndConditions;
  }

  /**
   * 
   * @param listeners Listeners to be notified about generation events.
   */
  public void setListeners(GenerationListenerList listeners) {
    this.listeners = listeners;
  }

  /**
   * Invoked to start the test generation using the configured parameters.
   *
   * @param fsm Describes the test model in an FSM format.
   */
  public void generate(FSM fsm) {
    suite = fsm.getTestSuite();
    log.debug("Starting test suite generation");
    beforeSuite(fsm);
    while (!checkSuiteEndConditions(fsm)) {
      log.debug("Starting new test generation");
      beforeTest(fsm);
      while (!checkTestCaseEndConditions(fsm)) {
        List<FSMTransition> enabled = getEnabled(fsm);
        FSMTransition next = algorithm.choose(suite, enabled);
        log.debug("Taking transition "+next.getName());
        execute(next);
        if (checkModelEndConditions(fsm)) {
          //stop this test case generation if any end condition returns true
          break;
        }
      }
      afterTest(fsm);
      log.debug("Finished new test generation");
    }
    afterSuite(fsm);
    log.debug("Finished test suite generation");
  }

  private boolean checkSuiteEndConditions(FSM fsm) {
    for (EndCondition ec : suiteEndConditions) {
      if (!ec.endNow(fsm, true)) {
        return false;
      }
    }
    return true;
  }

  private boolean checkTestCaseEndConditions(FSM fsm) {
    if (testEnding) {
      return checkEndStates(fsm);
    }
    for (EndCondition ec : testCaseEndConditions) {
      if (!ec.endNow(fsm, false)) {
        return false;
      }
    }
    testEnding = true;
    if (fsm.getEndStates().size() > 0) {
      return checkEndStates(fsm);
    }
    return true;
  }

  private boolean checkEndStates(FSM fsm) {
    Collection<InvocationTarget> endStates = fsm.getEndStates();
    for (InvocationTarget es : endStates) {
      Boolean endable = (Boolean)es.invoke();
      if (endable) {
        return true;
      }
    }
    return false;
  }

  /**
   * Calls every defind end condition and if any return true, also returns true. Otherwise, false.
   *
   * @param fsm The model object on which to invoke the methods.
   * @return true if current test case (not suite) generation should be stopped.
   */
  private boolean checkModelEndConditions(FSM fsm) {
    Collection<InvocationTarget> endConditions = fsm.getEndConditions();
    for (InvocationTarget ec : endConditions) {
      Boolean result = (Boolean)ec.invoke();
      if (result) {
        return true;
      }
    }
    return false;
  }

  private void beforeSuite(FSM fsm) {
    listeners.suiteStarted();
    Collection<InvocationTarget> befores = fsm.getBeforeSuites();
    invokeAll(befores);
  }

  private void afterSuite(FSM fsm) {
    Collection<InvocationTarget> afters = fsm.getAfterSuites();
    invokeAll(afters);
    listeners.suiteEnded();
  }

  private void beforeTest(FSM fsm) {
    //update history
    suite.startTest();
    listeners.testStarted();
    Collection<InvocationTarget> befores = fsm.getBefores();
    invokeAll(befores);
  }

  private void afterTest(FSM fsm) {
    Collection<InvocationTarget> afters = fsm.getAfters();
    invokeAll(afters);
    //update history
    suite.endTest();
    listeners.testEnded();
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
   * @param targets The methods to be invoked.
   * @param arg Argument to methods invoked.
   * @param element Type of model element (pre or post)
   * @param name Transition name for listener invocation.
   */
  private void invokeAll(Collection<InvocationTarget> targets, Object arg, String element, String name) {
    for (InvocationTarget target : targets) {
      if (element.equals("pre")) {
        listeners.pre(name);
      }
      if (element.equals("post")) {
        listeners.post(name);
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
   * @param fsm Describes the test model.
   * @return The list of enabled {@link osmo.tester.annotation.Transition} methods.
   */
  private List<FSMTransition> getEnabled(FSM fsm) {
    Collection<FSMTransition> allTransitions = fsm.getTransitions();
    List<FSMTransition> enabled = new ArrayList<FSMTransition>();
    enabled.addAll(allTransitions);
    for (FSMTransition transition : allTransitions) {
      for (InvocationTarget guard : transition.getGuards()) {
        listeners.guard(guard.getMethod().getName()+"("+transition.getName()+")");
        Boolean result = (Boolean)guard.invoke();
        if (!result) {
          enabled.remove(transition);
        }
      }
    }
    if (enabled.size() == 0) {
      throw new IllegalStateException("No transition available.");
    }
    return enabled;
  }

  /**
   * Executes the given transition on the given model.
   *
   * @param transition  The transition to be executed.
   */
  public void execute(FSMTransition transition) {
    transition.reset();
    //we have to add this first or it will produce failures..
    suite.add(transition);
    String name = transition.getName();
    invokeAll(transition.getPreMethods(), transition.getPrePostParameter(), "pre", name);
    listeners.transition(name);
    InvocationTarget target = transition.getTransition();
    target.invoke();
    invokeAll(transition.getPostMethods(), transition.getPrePostParameter(), "post", name);
  }

  public TestSuite getSuite() {
    return suite;
  }
}
