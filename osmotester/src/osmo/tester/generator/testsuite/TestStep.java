package osmo.tester.generator.testsuite;

import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Describes a single test step in a test case.
 * Difference to {@link FSMTransition} is that this includes the runtime information such as covered requirements.
 * 
 * @author Teemu Kanstren
 */
public class TestStep {
  /** The transition that was taken in this test step. */
  private final FSMTransition transition;
  /** The set of requirements covered by this test step. */
  private Collection<String> coveredRequirements = null;

  /**
   * Constructor.
   *
   * @param transition The transition that was taken in this test step.
   */
  public TestStep(FSMTransition transition) {
    this.transition = transition;
  }

  /**
   * Mark the given requirement as covered by this test step.
   *
   * @param requirement The identifier of the covered requirement.
   */
  public synchronized void covered(String requirement) {
    if (coveredRequirements == null) {
      coveredRequirements = new ArrayList<String>();
    }
    coveredRequirements.add(requirement);
  }

  /**
   * List of covered requirements by this test step.
   *
   * @return List of covered requirements by this test step.
   */
  public synchronized Collection<String> getCoveredRequirements() {
    if (coveredRequirements == null) {
      return Collections.EMPTY_LIST;
    }
    return coveredRequirements;
  }

  public FSMTransition getTransition() {
    return transition;
  }
}
