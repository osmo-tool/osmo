package osmo.tester.testmodels;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.StateName;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Variable;
import osmo.tester.coverage.CombinationCoverage;
import osmo.tester.coverage.RangeCategory;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueSet;

/** @author Teemu Kanstren */
public class RandomValueModel4 {
  private ValueRange<Integer> range = new ValueRange<>(0, 5);
  private ValueRange<Integer> range2 = new ValueRange<>(1, Integer.MAX_VALUE);
  private ValueSet<String> names = new ValueSet<>("teemu", "paavo", "keijo");
  private ValueSet<String> state = new ValueSet<>("state1", "state2");
  @TestSuiteField
  private TestSuite suite = null;
  @Variable
  private RangeCategory rangeRange = new RangeCategory(range).oneTwoMany();
  @Variable
  private RangeCategory range2Range = new RangeCategory(range2).zeroOneMany();
  @Variable
  private CombinationCoverage combo = new CombinationCoverage(names, rangeRange, range2Range);

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
  
  @StateName
  public String stateName(osmo.tester.generator.testsuite.TestStep step) {
    return state.next();
  }
}
