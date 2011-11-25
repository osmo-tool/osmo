package osmo.tester.scripting.manual;

import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.VariableField;
import osmo.tester.model.dataflow.SearchableInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/** @author Teemu Kanstren */
public class ScriptAlgorithm implements FSMTraversalAlgorithm {
  private List<TestScript> scripts = new ArrayList<TestScript>();
  private Iterator<TestScript> testIterator;
  private Iterator<ScriptStep> stepIterator;
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

  private void validate() {
    Collection<String> transitions = new ArrayList<String>();
    Collection<String> variables = new ArrayList<String>();
    Collection<FSMTransition> fsmTransitions = fsm.getTransitions();
    for (FSMTransition fsmTransition : fsmTransitions) {
      transitions.add(fsmTransition.getName());
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
    Collection<String> missingTransitions = new ArrayList<String>();
    Collection<String> missingVariables = new ArrayList<String>();
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
      errors += "Transitions "+missingTransitions+" not found in model.";
    }
    if (missingVariables.size() > 0) {
      errors += "Variables "+missingVariables+" not found in model.";
    }
    if (errors.length() > 0) {
      throw new IllegalStateException("Validating given script against given model objects failed:"+errors);
    }
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> transitions) {
    ScriptStep step = stepIterator.next();
    String transition = step.getTransition();
    return fsm.getTransition(transition);
  }

  public boolean isTestDone() {
    boolean done = !stepIterator.hasNext();
    if (done && testIterator.hasNext()) {
      stepIterator = testIterator.next().iterator();
    }
    return done;
  }

  public boolean isSuiteDone() {
    boolean stepsRemain = stepIterator.hasNext();
    boolean testsRemain = testIterator.hasNext();
    boolean done = !(stepsRemain || testsRemain);
    return done;
  }
}
