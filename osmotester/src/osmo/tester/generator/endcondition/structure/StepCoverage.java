package osmo.tester.generator.endcondition.structure;

import osmo.common.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * An end condition that defines a set of test steps that need to be covered.
 * The given set of required coverage can include duplicates, meaning the same step needs to be covered as many
 * times as it appears in the given set.
 *
 * @author Teemu Kanstren
 */
public class StepCoverage implements EndCondition {
  private static final Logger log = new Logger(StepCoverage.class);
  /** The names of the steps that need to be covered. */
  private Collection<String> required = new ArrayList<>();

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
    log.d("suite check");
    List<TestCase> allTests = suite.getAllTestCases();
    Collection<String> steps = new ArrayList<>();
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
    Collection<String> remaining = new ArrayList<>();
    log.d("steps:" + steps);
    remaining.addAll(required);
    for (String step : steps) {
      remaining.remove(step);
    }
    log.d("remaining:" + remaining);
    return remaining.size() == 0;
  }

  /**
   * Takes the set of steps from the given test case.
   *
   * @param test The test case to mine.
   * @return The names of covered steps for the given test case.
   */
  private Collection<String> stepsFor(TestCase test) {
    Collection<TestCaseStep> testSteps = test.getSteps();
    Collection<String> steps = new ArrayList<>();
    for (TestCaseStep step : testSteps) {
      steps.add(step.getName());
    }
    return steps;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    log.d("test check");
    TestCase test = suite.getCurrentTest();
    Collection<String> steps = stepsFor(test);
    return checkRequiredSteps(steps);
  }

  /**
   * Add a new test step requirement.
   *
   * @param step The new requirement.
   */
  public void addRequiredStep(String step) {
    required.add(step);
  }

  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
    Collection<FSMTransition> transitions = fsm.getTransitions();
    Collection<String> toClear = new LinkedHashSet<>();
    toClear.addAll(required);
    for (FSMTransition transition : transitions) {
      String name = transition.getStringName();
      boolean ok = toClear.remove(name);
    }
    if (!toClear.isEmpty()) {
      throw new IllegalStateException("Impossible coverage requirements, defined steps " + toClear + " not found.");
    }
  }

  @Override
  public String toString() {
    return "StepCoverage{" +
            "required=" + required +
            '}';
  }

  @Override
  public EndCondition cloneMe() {
    //we have no state to initialize so can just return self
    return this;
  }
}
