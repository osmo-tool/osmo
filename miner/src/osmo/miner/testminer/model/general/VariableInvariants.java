package osmo.miner.testminer.model.general;

import osmo.miner.testminer.model.dataflow.DataFlowInvariant;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public class VariableInvariants {
  private final String name;
  private final Collection<DataFlowInvariant> invariants = new ArrayList<>();

  public VariableInvariants(String name) {
    this.name = name;
  }

  public void add(DataFlowInvariant invariant) {
    invariants.add(invariant);
  }

  public String getName() {
    return name;
  }

  public Collection<DataFlowInvariant> getInvariants() {
    return invariants;
  }

  public int count() {
    return invariants.size();
  }
}
