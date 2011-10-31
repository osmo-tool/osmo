package osmo.tester.generator.endcondition.data;

import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class DataCoverage implements EndCondition {
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
      ModelVariable variable = variables.get(req.getName());
      Collection<Object> values = variable.getValues();
      for (Object value : values) {
        temp.remove(value);
      }
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
}
