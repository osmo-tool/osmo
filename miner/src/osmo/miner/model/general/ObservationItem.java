package osmo.miner.model.general;

import osmo.miner.model.dataflow.DataFlowInvariant;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public class ObservationItem {
  private final String scope;
  private final String variable;
  private final Collection<DataFlowInvariant> invariants = new ArrayList<DataFlowInvariant>();

  public ObservationItem(String scope, String variable) {
    this.scope = scope;
    this.variable = variable;
  }

  public String getScope() {
    return scope;
  }

  public String getVariable() {
    return variable;
  }


}
