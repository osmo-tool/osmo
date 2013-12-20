package osmo.tester.optimizer.reducer;

import osmo.tester.generator.testsuite.TestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class TestMetrics {
  private String lastStep = null;
  private Map<String, Integer> stepCounts = new HashMap<>();

  public TestMetrics() {
  }
  
  public TestMetrics(TestCase test) {
    List<String> steps = test.getAllStepNames();
    for (String step : steps) {
      process(step);
    }
  }

  public Map<String, Integer> getStepCounts() {
    return stepCounts;
  }
  
  private void process(String step) {
    Integer count = stepCounts.get(step);
    if (count == null) {
      count = 0;
    }
    count++;
    stepCounts.put(step, count);
    lastStep = step;
  }

  public String getLastStep() {
    return lastStep;
  }
}
