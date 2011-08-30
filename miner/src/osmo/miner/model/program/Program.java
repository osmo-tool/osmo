package osmo.miner.model.program;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Program {
  private final String name;
  private Map<String, Variable> variables = new HashMap<String, Variable>();
  private Map<String, Program> steps = new LinkedHashMap<String, Program>();

  public Program(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Map<String, Variable> getVariables() {
    return variables;
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
      step = new Program(name);
      steps.put(name, step);
    }
    return step;
  }
}
