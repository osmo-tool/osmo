package osmo.tester.optimizer.reducer;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;
import osmo.tester.optimizer.reducer.invariants.Precedence;
import osmo.tester.optimizer.reducer.invariants.SharedSequence;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class Invariants {
  private Map<String, Integer> mins = new HashMap<>();
  private Map<String, Integer> maxs = new HashMap<>();
  private Collection<String> lastSteps = new LinkedHashSet<>();
  private Map<String, Integer> stepCounts = new HashMap<>();
  private final Precedence precedence;
  private SharedSequence sequences = new SharedSequence();
  
  public Invariants(List<String> steps) {
    precedence = new Precedence(steps);
  }

  public void process(TestCase test) {
    List<String> steps = test.getAllStepNames();
    sequences.init(steps);
    precedence.process(steps);
    sequences.process(steps);
    TestMetrics metrics = new TestMetrics(test);
    stepCounts = metrics.getStepCounts();
    for (String step : stepCounts.keySet()) {
      int count = stepCounts.get(step);
      Integer min = mins.get(step);
      Integer max = maxs.get(step);
      if (min == null) min = Integer.MAX_VALUE;
      if (max == null) max = Integer.MIN_VALUE;
      if (count < min) mins.put(step, count);
      if (count > max) maxs.put(step, count);
      lastSteps.add(metrics.getLastStep());
    }
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
  
  public Collection<String> getPrecedencePatterns() {
    return precedence.getPatterns();
  }

  public Collection<String> getSequencePatterns() {
    return sequences.getPatterns();
  }
}
