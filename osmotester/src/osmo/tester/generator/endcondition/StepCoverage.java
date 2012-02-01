package osmo.tester.generator.endcondition;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * An end condition that defines a set of test steps (transitions) that need to be covered.
 *
 * @author Teemu Kanstren
 */
public class StepCoverage extends AbstractEndCondition {
  private static Logger log = new Logger(StepCoverage.class);
  /** The names of the steps (transitions) that need to be covered. */
  private Collection<String> required = new ArrayList<String>();

  /**
   * Initializes with given coverage criteria.
   *
   * @param stepsToCover The initial set of steps to be covered.
   */
  public StepCoverage(String... stepsToCover) {
    Collections.addAll(required, stepsToCover);
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    log.debug("suite check");
    List<TestCase> allTests = suite.getAllTestCases();
    Collection<String> steps = new ArrayList<String>();
    for (TestCase test : allTests) {
      steps.addAll(stepsFor(test));
    }
    return checkRequiredSteps(steps);
  }

  /**
   * Check if the given set of steps cover the set of defined requirements.
   * The given set of steps should reflect the set of steps taken so far (test generation trace).
   *
   * @param steps The set of covered steps.
   * @return True if all requirements are covered by the given steps.
   */
  private boolean checkRequiredSteps(Collection<String> steps) {
    Collection<String> remaining = new ArrayList<String>();
    log.debug("steps:" + steps);
    remaining.addAll(required);
    for (String step : steps) {
      remaining.remove(step);
    }
    log.debug("remaining:" + remaining);
    return remaining.size() == 0;
  }

  /**
   * Takes the set of steps from the given test case.
   *
   * @param test The test case to mine.
   * @return The names of covered steps for the given test case.
   */
  private Collection<String> stepsFor(TestCase test) {
    Collection<TestStep> testSteps = test.getSteps();
    Collection<String> steps = new ArrayList<String>();
    for (TestStep step : testSteps) {
      steps.add(step.getTransition().getName());
    }
    return steps;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    log.debug("test check");
    TestCase test = suite.getCurrentTest();
    Collection<String> steps = stepsFor(test);
    return checkRequiredSteps(steps);
  }

  /**
   * Add a new test step (transition) requirement.
   *
   * @param step The new requirement.
   */
  public void addRequiredStep(String step) {
    required.add(step);
  }

  @Override
  public void init(FSM fsm) {
    Collection<FSMTransition> transitions = fsm.getTransitions();
    Collection<String> toClear = new HashSet<String>();
    toClear.addAll(required);
    for (FSMTransition transition : transitions) {
      String name = transition.getName();
      boolean ok = toClear.remove(name);
    }
    if (!toClear.isEmpty()) {
      throw new IllegalStateException("Impossible coverage requirements, defined steps " + toClear + " not found.");
    }
  }
}
