package osmo.miner.model.program;

import osmo.miner.log.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Program {
  private static final Logger log = new Logger(Program.class);
  private final String name;
  private Map<String, Variable> variables = new HashMap<String, Variable>();
  private Map<String, Step> steps = new LinkedHashMap<String, Step>();

  public Program(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Map<String, Variable> getVariableMap() {
    return variables;
  }

  public List<Variable> getVariables() {
    List<Variable> result = new ArrayList<Variable>();
    result.addAll(variables.values());
    return result;
  }

  public Map<String, Variable> getGlobalVariableMap() {
    Map<String, Variable> globals = new HashMap<String, Variable>();
    for (Variable var : variables.values()) {
      String varName = var.getName();
      Variable gVar = new Variable(varName);
      globals.put(varName, gVar);
      Collection<String> values = var.getValues();
      for (String value : values) {
        gVar.addValue(value);
      }
    }
    for (Step step : steps.values()) {
      List<Variable> stepVariables = step.getVariables();
      for (Variable var : stepVariables) {
        String varName = var.getName();
        Variable gVar = globals.get(varName);
        if (gVar == null) {
          gVar = new Variable(varName);
          globals.put(varName, gVar);
        }
        Collection<String> values = var.getValues();
        for (String value : values) {
          gVar.addValue(value);
        }
      }
    }
    return globals;
  }

  public List<Variable> getGlobalVariables() {
    List<Variable> result = new ArrayList<Variable>();
    Map<String, Variable> globalVariableMap = getGlobalVariableMap();
    result.addAll(globalVariableMap.values());
    return result;
  }

  public Variable createVariable(String name) {
    Variable var = variables.get(name);
    if (var == null) {
      var = new Variable(name);
      variables.put(name, var);
    }
    return var;
  }

  public List<Step> getSteps() {
    List<Step> result = new ArrayList<Step>();
    result.addAll(steps.values());
    return result;
  }

  public Map<String, Step> getStepMap() {
    return steps;
  }

  public Step createStep(String name) {
    Step step = steps.get(name);
    if (step == null) {
      step = new Step(this, name);
      steps.put(name, step);
    }
    return step;
  }

  @Override
  public String toString() {
    return "Program{" +
            "name='" + name + '\'' +
            ", steps=" + steps +
            '}';
  }
}
