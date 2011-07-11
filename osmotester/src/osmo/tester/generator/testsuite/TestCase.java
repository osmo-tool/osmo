package osmo.tester.generator.testsuite;

import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * This class describes a single test case and all test steps that it contains.
 * This also includes a list of added coverage for model transitions and requirements.
 * Note that this coverage information may or may not hold in current testing.
 * If offline test mode is applied and
 * test suite content or ordering is optimized after generation, this information is likely invalid
 * (unless optimization updates this information in the test case objects as the supplies optimizers do).
 *
 * @author Teemu Kanstren
 */
public class TestCase {
  /** The test steps (taken) for this test case. */
  private List<TestStep> steps = new ArrayList<TestStep>();
  /** The latest test step (being/having been generated). */
  private TestStep currentStep = null;
  /** Newly covered transitions in relation to generation history. See class header for notes.
  *  NOTE: we use a Set to avoid duplicates if the same transition is covered multiple times. */
  private Collection<FSMTransition> addedTransitionCoverage = new HashSet<FSMTransition>();
  /** Newly covered requirements in relation to generation history. See class header for notes.
   *  NOTE: we use a Set to avoid duplicates if the same requirement is covered multiple times. */
  private Collection<String> addedRequirementsCoverage = new HashSet<String>();

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

  /**
   * Clear list of added transitions and requirements coverage.
   * Useful in test suite optimization when these lists need to be updated.
   */
  public void resetCoverage() {
    addedRequirementsCoverage.clear();
    addedTransitionCoverage.clear();
  }

  public Collection<FSMTransition> getAddedTransitionCoverage() {
    return addedTransitionCoverage;
  }

  public void addAddedTransitionCoverage(FSMTransition transition) {
    addedTransitionCoverage.add(transition);
  }

  public Collection<String> getAddedRequirementsCoverage() {
    return addedRequirementsCoverage;
  }

  public void addAddedRequirementsCoverage(String requirement) {
    addedRequirementsCoverage.add(requirement);
  }

  @Override
  public String toString() {
    return "TestCase{" +
            "steps=" + steps +
            '}';
  }
}
