package osmo.miner.model.program;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Program {
  private final Program parent;
  private final String name;
  private Map<String, Variable> variables = new HashMap<String, Variable>();
  //todo: this ordering needs to be reconsidered
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

  public Map<String, Variable> getVariables() {
    return variables;
  }

  public Map<String, Variable> getGlobalVariables() {
    Map<String, Variable> globals = new HashMap<String, Variable>();
    for (Variable var : variables.values()) {
      globals.put(var.getName(), var);
    }
    for (Program step : steps.values()) {
      Map<String, Variable> stepVariables = step.getVariables();
      for (String name : stepVariables.keySet()) {
        Variable var = globals.get(name);
        if (var == null) {
          var = new Variable(name);
          globals.put(name, var);
        }
        Collection<String> values = stepVariables.get(name).getValues();
        for (String value : values) {
          var.addValue(value);
        }
      }
    }
    return globals;
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
