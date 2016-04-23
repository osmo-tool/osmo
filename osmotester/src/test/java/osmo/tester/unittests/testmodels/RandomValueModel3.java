package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Variable;
import osmo.tester.coverage.RangeCategory;
import osmo.tester.model.data.ValueRange;

/** @author Teemu Kanstren */
public class RandomValueModel3 {
  @Variable
  private RangeCategory rc2 = new RangeCategory().zeroOneMany();

  private ValueRange<Integer> range = new ValueRange<>(0, 5);

  @TestStep("Step1")
  public void step() {
    rc2.process(range.random());
  }

  @TestStep("Step2")
  public void step2() {
    rc2.process(range.random());
  }
}
