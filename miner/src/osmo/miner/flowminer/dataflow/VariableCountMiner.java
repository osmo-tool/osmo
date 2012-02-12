package osmo.miner.flowminer.dataflow;

import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class VariableCountMiner {
  private Map<String, Integer> counts = new HashMap<>();

  public void process(String name, String value) {
    Integer count = counts.get(name);
    if (count == null) {
      count = 0;
    }
    counts.put(name, count+1);
  }

  public Map<String, Integer> getCounts() {
    return counts;
  }
}
