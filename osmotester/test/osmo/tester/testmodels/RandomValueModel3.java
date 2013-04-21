package osmo.tester.testmodels;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Variable;
import osmo.tester.coverage.RangeCategory2;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueSet;

/** @author Teemu Kanstren */
public class RandomValueModel3 {
  @Variable
  private RangeCategory2 rc2 = new RangeCategory2().zeroOneManyRanges();

  private ValueRange<Integer> range = new ValueRange<>(0, 5);

  @TestStep("Step1")
  public void step() {
    rc2.process(range.next());
  }

  @TestStep("Step2")
  public void step2() {
    rc2.process(range.next());
  }
}
