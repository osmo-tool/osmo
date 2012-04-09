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

/**
 * A coverage end condition that allows one to define a set of values for a set of given variables that
 * need to be covered by the test/suite.
 *
 * @author Teemu Kanstren
 */
public class DataCoverage extends AbstractEndCondition {
  private static Logger log = new Logger(DataCoverage.class);
  /** Coverage requirements for this end condition. */
  private Map<String, DataCoverageRequirement> requirements = new HashMap<>();

  /**
   * Adds a new coverage requirement that needs to be covered for the end condition to evaluate as true.
   *
   * @param requirement The requirement to add.
   */
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

  /**
   * Checks if the specified requirements have been covered.
   *
   * @param variables The variable values to be checked against requirements.
   * @return True if all the requirements of this end condition are covered by the given variable values.
   */
  private boolean checkRequirements(Map<String, ModelVariable> variables) {
    if (!variables.keySet().containsAll(requirements.keySet())) {
      //some variable is not covered at all
      return false;
    }
    for (DataCoverageRequirement req : requirements.values()) {
      Collection<Object> temp = new ArrayList<>();
      temp.addAll(req.getValues());
      String name = req.getName();
      log.debug("name:"+name);
      ModelVariable variable = variables.get(name);
      Collection<Object> values = variable.getValues();
      if (req.isAny()) {
        //any observed value is enough
        if (values.size() > 0) {
          continue;
        }
      }
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
  /**
   * Checks  that the requirements defined can be covered by the given test model.
   */
  public void init(FSM fsm) {
    Collection<String> shouldClear = new ArrayList<>();
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
