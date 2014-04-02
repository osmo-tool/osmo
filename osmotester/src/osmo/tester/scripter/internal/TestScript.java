package osmo.tester.scripter.internal;

import osmo.tester.scenario.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class TestScript {
  private Long seed = null;
  private final List<String> steps = new ArrayList<>();

  public TestScript() {
  }

  public void setSeed(long seed) {
    this.seed = seed;
  }

  public void addStep(String step) {
    steps.add(step);
  }

  public Long getSeed() {
    return seed;
  }

  public List<String> getSteps() {
    return steps;
  }
  
  public Scenario toScenario() {
    Scenario scenario = new Scenario(true);
    scenario.addStartup(steps.toArray(new String[steps.size()]));
    return scenario;
  }

  @Override
  public String toString() {
    return "TestScript{" +
            "seed=" + seed +
            ", steps=" + steps +
            '}';
  }
}
