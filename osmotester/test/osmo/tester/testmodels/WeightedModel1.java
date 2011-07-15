package osmo.tester.testmodels;

import osmo.tester.annotation.Transition;

/**
 * @author Teemu Kanstren
 */
public class WeightedModel1 {
  @Transition(name="bob2", weight=2)
  public void hello() {

  }

  @Transition(name="bob3", weight=3)
  public void world() {

  }

  @Transition(name="bob4", weight=4)
  public void xyz() {

  }

  @Transition("bob1")
  public void abc() {

  }
}
