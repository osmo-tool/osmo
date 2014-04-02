package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueSet;

/** @author Teemu Kanstren */
public class RandomValueModel2 {
  @Variable
  private ValueRange<Integer> range = new ValueRange<>(0, 5);
  @Variable
  private ValueRange<Integer> range2 = new ValueRange<>(1, Integer.MAX_VALUE);
  @Variable
  private ValueSet<String> names = new ValueSet<>("teemu", "paavo", "keijo");
  private TestSuite suite = null;

  @TestStep("Step1")
  public void step() {
    range.next();
  }

  @TestStep("Step2")
  public void step2() {
    range2.next();
  }

  @TestStep("Step3")
  public void step3() {
    names.next();
  }
  
  @AfterTest
  public void finish() {
    suite.addValue("teemu", "on paras");
  }
}
