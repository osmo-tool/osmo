package osmo.tester.generator.testsuite;

import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.VariableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes a single test step in a test case.
 * Difference to {@link FSMTransition} is that this includes the runtime information such as covered requirements.
 * Model state variable values (annotated with {@link osmo.tester.annotation.Variable}) are stored at the end of
 * the executed transition function.
 * 
 * @author Teemu Kanstren
 */
public class TestStep {
  /** The transition that was taken in this test step. */
  private final FSMTransition transition;
  /** The set of requirements covered by this test step. */
  private Collection<String> coveredRequirements = null;
  /** Stores values of all {@link osmo.tester.annotation.Variable} annotated fields for this step. */
  private Map<String, Object> stateValues = new HashMap<String, Object>();

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
    Collection<String> result = new ArrayList<String>();
    result.addAll(coveredRequirements);
    return result;
  }

  public FSMTransition getTransition() {
    return transition;
  }

  @Override
  public String toString() {
    return "TestStep{" +
            "transition=" + transition +
            '}';
  }

  public void storeState(FSM fsm) {
    Collection<VariableField> variables = fsm.getStateVariables();
    for (VariableField variable : variables) {
      stateValues.put(variable.getName(), variable.getValue());
    }
  }
}
