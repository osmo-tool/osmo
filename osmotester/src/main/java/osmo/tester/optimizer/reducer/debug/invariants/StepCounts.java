package osmo.tester.optimizer.reducer.debug.invariants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Counts the number of times each step has been observed in all traces.
 * If every trace has step A 1 times, the value for A is 1.
 * If some have A 1 times and others never, the value for A is 0-1.
 * If some have A 1 others A 2 and one A 4 times, the value for A is 1-4.
 * If A is never observed there is no value for it. It should rather be listed as missing in the report.
 * 
 * @author Teemu Kanstren
 */
public class StepCounts {
  /** Key = Step name, Value = Times observed, min and max. */
  private Map<String, StepCount> stepCounts = new LinkedHashMap<>();

  /**
   * @param allSteps All steps in the model.
   */
  public StepCounts(List<String> allSteps) {
    for (String step : allSteps) {
      stepCounts.put(step, new StepCount(step));
    }
  }

  /**
   * Counts how many times given test trace has each step, updating the counter states.
   * 
   * @param steps For test case to process.
   */
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

  /**
   * Gives the numbers for steps that have been observed at least once.
   * 
   * @return Counts for observed steps. Form is name + count as string.
   */
  public Collection<String> getUsedStepCounts() {
    Collection<String> strs = new ArrayList<>();
    for (StepCount stepCount : stepCounts.values()) {
      if (stepCount.max > 0) strs.add(stepCount.toString());
    }
    return strs;
  }

  /**
   * Gives list of steps never observed.
   * 
   * @return List of step names never observed.
   */
  public Collection<String> getMissingStepCounts() {
    Collection<String> strs = new ArrayList<>();
    for (StepCount stepCount : stepCounts.values()) {
      if (stepCount.max == 0) strs.add(stepCount.name);
    }
    return strs;
  }

  /**
   * For maintaining information on minimum and maximum times a step has been observed.
   */
  private static class StepCount {
    /** Name of step. */
    private final String name;
    /** Smallest number of times observed in a test trace. */
    private int min = Integer.MAX_VALUE;
    /** Largest number of times observed in a test trace. */
    private int max = Integer.MIN_VALUE;

    private StepCount(String name) {
      this.name = name;
    }

    /**
     * Update min and max of observed for this step.
     * 
     * @param counts How many times current trace has this step.
     */
    public void process(Map<String, Integer> counts) {
      Integer count = counts.get(name);
      if (count == null) count = 0;
      if (count < min) min = count;
      if (count > max) max = count;
    }

    /**
     * Builds a string representation for reporting.
     * 
     * @return Name and min/max counts as string.
     */
    public String toString() {
      String str = name + " : ";
      if (min == max) return str + min;
      return str + min + "-"+max;
    }
  }
}
