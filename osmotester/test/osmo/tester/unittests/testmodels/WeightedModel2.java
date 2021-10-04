package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
public class WeightedModel2 {
  //integer max_value is expected to be 2_147_483_647
  //weighted algo should multiply it by 10_000 if any weight in model is below 10
  //in which case, this should overflow the weight if not considered
  @TestStep(name = "bob2", weight = 2_147_483)
  public void hello() {

  }

  @TestStep(name = "bob3", weight = 3)
  public void world() {

  }

  @TestStep(name = "bob4", weight = 4)
  public void xyz() {

  }

  @TestStep(name = "bob1", weight = 1)
  public void abc() {

  }
}
