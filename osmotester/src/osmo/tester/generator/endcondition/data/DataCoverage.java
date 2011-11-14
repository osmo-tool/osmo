package osmo.tester.generator.endcondition.data;

import osmo.common.log.Logger;
import osmo.tester.generator.endcondition.AbstractEndCondition;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.VariableField;
import osmo.tester.model.dataflow.SearchableInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class DataCoverage extends AbstractEndCondition {
  private static Logger log = new Logger(DataCoverage.class);
  private Map<String, DataCoverageRequirement> requirements = new HashMap<String, DataCoverageRequirement>();

  public void addRequirement(DataCoverageRequirement requirement) {
    String variable = requirement.getName();
    if (requirements.containsKey(variable)) {
      throw new IllegalArgumentException("Requirement for " + variable + " already defined. Only one supported.");
    }
    requirements.put(variable, requirement);
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    Map<String, ModelVariable> variables = suite.getVariables();
    return checkRequirements(variables);
  }

  private boolean checkRequirements(Map<String, ModelVariable> variables) {
    if (!variables.keySet().containsAll(requirements.keySet())) {
      //some variable is not covered at all
      return false;
    }
    for (DataCoverageRequirement req : requirements.values()) {
      Collection<Object> temp = new ArrayList<Object>();
      temp.addAll(req.getValues());
      String name = req.getName();
      ModelVariable variable = variables.get(name);
      Collection<Object> values = variable.getValues();
      log.debug("values:" + values);
      for (Object value : values) {
        temp.remove("" + value);
      }
      log.debug("temp:" + temp);
      if (temp.size() > 0) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    TestCase test = suite.getCurrentTest();
    Map<String, ModelVariable> variables = test.getVariables();
    return checkRequirements(variables);
  }

  @Override
  public void init(FSM fsm) {
    Collection<String> shouldClear = new ArrayList<String>();
    shouldClear.addAll(requirements.keySet());
    Collection<SearchableInput> inputs = fsm.getSearchableInputs();
    for (SearchableInput input : inputs) {
      String name = input.getName();
      log.debug("Input:" + name);
      shouldClear.remove(name);
      if (!input.isAllSupported()) {
        continue;
      }
      DataCoverageRequirement req = requirements.get(name);
      if (req != null) {
        if (req.isAll() && !req.isInitialized()) {
          req.initializeFrom(input);
        }
        Collection<String> values = req.getValues();
        for (Object value : values) {
          if (!input.evaluateSerialized("" + value)) {
            throw new IllegalArgumentException("Impossible coverage requirements, defined variables [" + name + "] can not have value " + value + ".");
          }
        }
      }
    }
    Collection<VariableField> stateVariables = fsm.getStateVariables();
    for (VariableField variable : stateVariables) {
      shouldClear.remove(variable.getName());
    }
    if (shouldClear.size() > 0) {
      throw new IllegalStateException("Impossible coverage requirements, defined variables " + shouldClear + " not found.");
    }
  }
}
