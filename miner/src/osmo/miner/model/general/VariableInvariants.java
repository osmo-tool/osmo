package osmo.miner.model.general;

import osmo.miner.model.dataflow.DataFlowInvariant;
import osmo.miner.model.dataflow.EmptyInvariant;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public class VariableInvariants {
  private final String name;
  private final Collection<DataFlowInvariant> invariants = new ArrayList<DataFlowInvariant>();
  private static final Collection<DataFlowInvariant> empty = new ArrayList<DataFlowInvariant>();

  static {
    empty.add(new EmptyInvariant("", ""));
  }

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
    System.out.println("name:"+name+" size:"+count());
    if (invariants.size() == 0) {
      return empty;
    }
    return invariants;
  }

  public int count() {
    return invariants.size();
  }
}
