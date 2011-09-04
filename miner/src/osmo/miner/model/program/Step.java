package osmo.miner.model.program;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Step {
  private final Program parent;
  private final String name;
  private Map<String, String> variables = new HashMap<String, String>();

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

  public void addVariable(String name, String value) {
    variables.put(name, value);
  }

  public Map<String, String> getVariables() {
    return variables;
  }

  public Step deepCopy(Program parent) {
    Step copy = new Step(parent, name);
    for (String name : variables.keySet()) {
      copy.addVariable(name, variables.get(name));
    }
    return copy;
  }
}
