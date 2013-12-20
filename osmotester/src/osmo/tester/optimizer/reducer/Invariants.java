package osmo.tester.optimizer.reducer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Invariants {
  private Map<String, Integer> mins = new HashMap<>();
  private Map<String, Integer> maxs = new HashMap<>();
  private Collection<String> lastSteps = new LinkedHashSet<>();
  private Map<String, Integer> stepCounts = new HashMap<>();

  public void process(TestMetrics metrics) {
    Map<String, Integer> counts = metrics.getStepCounts();
    for (String step : counts.keySet()) {
      int count = counts.get(step);
      Integer min = mins.get(step);
      Integer max = maxs.get(step);
      if (min == null) min = Integer.MAX_VALUE;
      if (max == null) max = Integer.MIN_VALUE;
      if (count < min) mins.put(step, count);
      if (count > max) maxs.put(step, count);
      lastSteps.add(metrics.getLastStep());
    }
    stepCounts = metrics.getStepCounts();
  }

  public int minFor(String step) {
    Integer min = mins.get(step);
    if (min == null) throw new NullPointerException("No min found for "+step);
    return min;
  }

  public int maxFor(String step) {
    Integer max = maxs.get(step);
    if (max == null) throw new NullPointerException("No max found for "+step);
    return max;
  }

  public Collection<String> getLastSteps() {
    return lastSteps;
  }

  public Map<String, Integer> getStepCounts() {
    return stepCounts;
  }
  
  public String getPatterns() {
    return "";
  }
}
