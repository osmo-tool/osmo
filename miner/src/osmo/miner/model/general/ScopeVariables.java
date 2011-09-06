package osmo.miner.model.general;

import osmo.miner.model.dataflow.DataFlowInvariant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class ScopeVariables {
  private final String name;
  private final boolean program;
  private final boolean global;
  private final Map<String, VariableInvariants> variables = new HashMap<String, VariableInvariants>();

  public ScopeVariables(String name, boolean program, boolean global) {
    this.name = name;
    this.program = program;
    this.global = global;
  }

  public void add(DataFlowInvariant invariant) {
    String variable = invariant.getVariable();
    VariableInvariants data = variables.get(variable);
    if (data == null) {
      data = new VariableInvariants(variable);
      variables.put(variable, data);
    }
    data.add(invariant);
  }

  public boolean isProgram() {
    return program;
  }

  public boolean isGlobal() {
    return global;
  }

  public VariableInvariants getInvariantsFor(String variable) {
    return variables.get(variable);
  }

  public String getName() {
    return name;
  }

  public Collection<VariableInvariants> getVariables() {
    return variables.values();
  }
}
