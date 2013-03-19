package osmo.miner.flowminer.dataflow;

import osmo.miner.flowminer.model.ValueRange;
import osmo.miner.testminer.model.dataflow.ValueRangeInt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** @author Teemu Kanstren */
public class ValueRangeMiner {
  private Map<String, ValueRange> ranges = new HashMap<>();

  public void process(String name, String value) {
    ValueRange range = ranges.get(name);
    if (range == null) {
      range = new ValueRange(name);
      ranges.put(name, range);
    }
    if (!range.isValid()) {
      return;
    }
    range.check(value);
  }

  public Map<String, ValueRange> getRanges() {
    Map<String, ValueRange> result = new HashMap<>();
    for (ValueRange range : ranges.values()) {
      if (range.isValid()) {
        result.put(range.getName(), range);
      }
    }
    return result;
  }
}
