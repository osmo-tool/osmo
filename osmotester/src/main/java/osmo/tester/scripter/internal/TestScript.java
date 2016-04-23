package osmo.tester.scripter.internal;

import osmo.tester.scenario.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * A test script that the generator can execute on a matching model instance.
 * Allows just saving the seed and sequence of step names to re-produce whole test execution.
 * Of course, you also need to save the model and generator versions..
 * 
 * @author Teemu Kanstren
 */
public class TestScript {
  /** Seed for generator. */
  private Long seed = null;
  /** Sequence of steps the generator should take. */
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
