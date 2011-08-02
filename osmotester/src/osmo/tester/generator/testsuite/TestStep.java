package osmo.tester.generator.testsuite;

import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.VariableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import osmo.tester.annotation.Variable;

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
  /** Stores values of all {@link Variable} annotated fields in the model before this step was generated. */
  private Map<String, Object> stateValuesBefore = new HashMap<String, Object>();
  /** Stores values of all {@link Variable} annotated fields in the model after this step was generated. */
  private Map<String, Object> stateValuesAfter = new HashMap<String, Object>();

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

  public Map<String, Object> getStateValuesBefore() {
    return stateValuesBefore;
  }

  public Map<String, Object> getStateValuesAfter() {
    return stateValuesAfter;
  }

  /**
   * Stores the model state that was before the transition is executed.
   *
   * @param fsm This is where the state is copied from.
   */
  public void storeStateBefore(FSM fsm) {
    Collection<VariableField> variables = fsm.getStateVariables();
    for (VariableField variable : variables) {
      stateValuesBefore.put(variable.getName(), variable.getValue());
    }
  }

  /**
   * Stores the model state that is after the transition has been executed.
   *
   * @param fsm This is where the state is copied from.
   */
  public void storeStateAfter(FSM fsm) {
    Collection<VariableField> variables = fsm.getStateVariables();
    for (VariableField variable : variables) {
      stateValuesAfter.put(variable.getName(), variable.getValue());
    }
  }
}
