package osmo.tester.generator.testsuite;

import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes a single test case and all test steps that it contains.
 * 
 * @author Teemu Kanstren
 */
public class TestCase {
  /** The test steps (taken) for this test case. */
  private List<TestStep> steps = new ArrayList<TestStep>();
  /** The latest test step (being/having been generated). */
  private TestStep currentStep = null;

  /**
   * Adds a new test step.
   *
   * @param transition The transition for the test step. 
   */
  public void addTransition(FSMTransition transition) {
    TestStep step = new TestStep(transition);
    steps.add(step);
    currentStep = step;
  }

  /**
   * Defines that the current test step covered the given requirement.
   *
   * @param requirement The covered requirement identifier.
   */
  public void covered(String requirement) {
    currentStep.covered(requirement);
  }

  /**
   * Get list of test steps generated (so far) for this test case.
   *
   * @return List of test steps (transitions).
   */
  public List<TestStep> getSteps() {
    return steps;
  }
}
