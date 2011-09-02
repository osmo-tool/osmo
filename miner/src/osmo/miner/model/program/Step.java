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
public class Step {
  private final Program parent;
  private final String name;
  private Map<String, Variable> variables = new HashMap<String, Variable>();

  public Step(Program parent, String name) {
    this.parent = parent;
    this.name = name;
  }

  public Program getParent() {
    return parent;
  }

  public String getName() {
    return name;
  }

  public Variable createVariable(String name) {
    Variable var = variables.get(name);
    if (var == null) {
      var = new Variable(name);
      variables.put(name, var);
    }
    return var;
  }

  public Map<String, Variable> getVariableMap() {
    return variables;
  }

  public List<Variable> getVariables() {
    List<Variable> result = new ArrayList<Variable>();
    result.addAll(variables.values());
    return result;
  }

  public Step deepCopy(Program parent) {
    Step copy = new Step(parent, name);
    for (Variable v : variables.values()) {
      Variable cv = copy.createVariable(v.getName());
      Collection<String> values = v.getValues();
      for (String value : values) {
        cv.addValue(value);
      }
    }
    return copy;
  }
}
