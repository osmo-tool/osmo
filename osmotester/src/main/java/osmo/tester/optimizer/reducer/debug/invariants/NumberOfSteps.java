package osmo.tester.optimizer.reducer.debug.invariants;

import osmo.tester.generator.testsuite.TestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Number of times different steps were taken in given tests.
 * 
 * @author Teemu Kanstren
 */
public class NumberOfSteps {
  /** Key = step name, Value = number of times step was taken. */
  private Map<String, Integer> stepCounts = new HashMap<>();

  public NumberOfSteps(TestCase test) {
    List<String> steps = test.getAllStepNames();
    for (String step : steps) {
      process(step);
    }
  }

  public Map<String, Integer> getStepCounts() {
    return stepCounts;
  }
  
  private void process(String step) {
    int count = stepCounts.getOrDefault(step, 0);
    count++;
    stepCounts.put(step, count);
  }
}
