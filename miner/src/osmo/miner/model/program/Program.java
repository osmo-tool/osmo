package osmo.miner.model.program;

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
  private final Program parent;
  private final String name;
  private Map<String, Variable> variables = new HashMap<String, Variable>();
  private Map<String, Program> steps = new LinkedHashMap<String, Program>();

  public Program(Program parent, String name) {
    this.parent = parent;
    this.name = name;
  }

  public Program getParent() {
    return parent;
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
      globals.put(var.getName(), var);
    }
    for (Program step : steps.values()) {
      List<Variable> stepVariables = step.getVariables();
      for (Variable var : stepVariables) {
        if (globals.get(var.getName()) == null) {
          globals.put(var.getName(), var);
        }
        Collection<String> values = var.getValues();
        for (String value : values) {
          var.addValue(value);
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

  public Map<String, Program> getSteps() {
    return steps;
  }

  public Program createStep(String name) {
    Program step = steps.get(name);
    if (step == null) {
      step = new Program(this, name);
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
