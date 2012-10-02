package osmo.tester.scripting.manual;

import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.VariableField;
import osmo.tester.model.dataflow.SearchableInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A test generation algorithm for running manually defined scripts.
 *
 * @author Teemu Kanstren
 */
public class ScriptAlgorithm implements FSMTraversalAlgorithm {
  /** The scripts to run. */
  private List<TestScript> scripts = new ArrayList<>();
  /** For iterating over the tests to be executed. */
  private Iterator<TestScript> testIterator;
  /** For iterating the steps in current test cases. */
  private Iterator<ScriptStep> stepIterator;
  /** The FSM based on which the tests will be executed. */
  private FSM fsm;

  public void addScript(TestScript script) {
    scripts.add(script);
  }

  @Override
  public void init(FSM fsm) {
    this.fsm = fsm;
    validate();
    testIterator = scripts.iterator();
    stepIterator = testIterator.next().iterator();
  }

  /** Validate the test scripts against the FSM. */
  private void validate() {
    Collection<String> transitions = new ArrayList<>();
    Collection<String> variables = new ArrayList<>();
    Collection<FSMTransition> fsmTransitions = fsm.getTransitions();
    for (FSMTransition fsmTransition : fsmTransitions) {
      transitions.add(fsmTransition.getStringName());
    }
    Collection<SearchableInput> inputs = fsm.getSearchableInputs();
    for (SearchableInput input : inputs) {
      variables.add(input.getName());
    }
    Collection<VariableField> stateVariables = fsm.getStateVariables();
    for (VariableField variable : stateVariables) {
      variables.add(variable.getName());
    }

    String errors = "";
    Collection<String> missingTransitions = new ArrayList<>();
    Collection<String> missingVariables = new ArrayList<>();
    for (TestScript script : scripts) {
      for (ScriptStep step : script) {
        String name = step.getTransition();
        if (!transitions.contains(name)) {
          missingTransitions.add(name);
        }
        List<ScriptValue> values = step.getValues();
        for (ScriptValue value : values) {
          String variable = value.getVariable();
          if (!variables.contains(variable)) {
            missingVariables.add(variable);
          }
        }
      }
    }
    if (missingTransitions.size() > 0) {
      errors += "Transitions " + missingTransitions + " not found in model.";
    }
    if (missingVariables.size() > 0) {
      errors += "Variables " + missingVariables + " not found in model.";
    }
    if (errors.length() > 0) {
      throw new IllegalStateException("Validating given script against given model objects failed:" + errors);
    }
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> transitions) {
    ScriptStep step = stepIterator.next();
    String transition = step.getTransition();
    return fsm.getTransition(transition);
  }

  /**
   * Check if current test is finished.
   *
   * @return True if done.
   */
  public boolean isTestDone() {
    boolean done = !stepIterator.hasNext();
    if (done && testIterator.hasNext()) {
      stepIterator = testIterator.next().iterator();
    }
    return done;
  }

  /**
   * Check if suite is finished.
   *
   * @return True if done.
   */
  public boolean isSuiteDone() {
    boolean stepsRemain = stepIterator.hasNext();
    boolean testsRemain = testIterator.hasNext();
    boolean done = !(stepsRemain || testsRemain);
    return done;
  }
}
