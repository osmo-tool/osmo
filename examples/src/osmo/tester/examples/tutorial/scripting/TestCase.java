package osmo.tester.examples.tutorial.scripting;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class TestCase {
  private Collection<TestStep> steps = new ArrayList<>();

  public void addStep(TestStep step) {
    steps.add(step);
  }

  public Collection<TestStep> getSteps() {
    return steps;
  }
}
