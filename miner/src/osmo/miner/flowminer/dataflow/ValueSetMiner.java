package osmo.miner.flowminer.dataflow;

import osmo.miner.flowminer.model.ValueSet;

import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class ValueSetMiner {
  private Map<String, ValueSet> sets = new HashMap<>();

  public void process(String name, String value) {
    ValueSet set = sets.get(name);
    if (set == null) {
      set = new ValueSet(name);
      sets.put(name, set);
    }
    set.add(value);
  }

  public Map<String, ValueSet> getSets() {
    return sets;
  }
}
