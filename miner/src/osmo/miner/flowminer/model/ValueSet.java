package osmo.miner.flowminer.model;

import java.util.HashMap;
import java.util.Map;

/** @author Teemu Kanstren */
public class ValueSet {
  private final String name;
  private Map<String, Integer> values = new HashMap<String, Integer>();

  public ValueSet(String name) {
    this.name = name;
  }

  public void add(String value) {
    Integer count = values.get(value);
    if (count == null) {
      count = 0;
    }
    values.put(value, count + 1);
  }

  public Map<String, Integer> getValues() {
    return values;
  }

  public String getName() {
    return name;
  }
}
