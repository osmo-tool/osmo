package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Variable;
import osmo.tester.model.data.ValueRange;

/** @author Teemu Kanstren */
public class RandomValueModel {
  @Variable
  private ValueRange<Integer> range = new ValueRange<>(1, Integer.MAX_VALUE); 
  
  @TestStep("Step")
  public void step() {
    range.random();
  }
}
