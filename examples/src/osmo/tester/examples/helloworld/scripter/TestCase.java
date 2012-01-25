package osmo.tester.examples.helloworld.scripter;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class TestCase {
  private Collection<TestStep> steps = new ArrayList<TestStep>();

  public void addStep(TestStep step) {
    steps.add(step);
  }

  public Collection<TestStep> getSteps() {
    return steps;
  }
}
