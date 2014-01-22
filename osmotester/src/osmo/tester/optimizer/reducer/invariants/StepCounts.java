package osmo.tester.optimizer.reducer.invariants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class StepCounts {
  private Map<String, StepCount> stepCounts = new LinkedHashMap<>();

  public StepCounts(List<String> allSteps) {
    for (String step : allSteps) {
      stepCounts.put(step, new StepCount(step));
    }
  }

  public void process(List<String> steps) {
    Map<String, Integer> counts = new HashMap<>();
    for (String step : steps) {
      Integer count = counts.get(step);
      if (count == null) {
        count = 0;
      }
      count++;
      counts.put(step, count);
    }
    for (StepCount stepCount : stepCounts.values()) {
      stepCount.process(counts);
    }
  }

  public Collection<String> getUsedStepCounts() {
    Collection<String> strs = new ArrayList<>();
    for (StepCount stepCount : stepCounts.values()) {
      if (stepCount.max > 0) strs.add(stepCount.toString());
    }
    return strs;
  }

  public Collection<String> getMissingStepCounts() {
    Collection<String> strs = new ArrayList<>();
    for (StepCount stepCount : stepCounts.values()) {
      if (stepCount.max == 0) strs.add(stepCount.name);
    }
    return strs;
  }

  private static class StepCount {
    private final String name;
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    private StepCount(String name) {
      this.name = name;
    }

    public void process(Map<String, Integer> counts) {
      Integer count = counts.get(name);
      if (count == null) count = 0;
      if (count < min) min = count;
      if (count > max) max = count;
    }

    public String toString() {
      String str = name + " : ";
      if (min == max) return str + min;
      return str + min + "-"+max;
    }
  }
}
