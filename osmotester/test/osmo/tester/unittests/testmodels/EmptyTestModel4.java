package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.CoverageValue;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.LastStep;
import osmo.tester.annotation.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

/** @author Teemu Kanstren */
public class EmptyTestModel4 {
  private Requirements req = null;
  private TestSuite suite = null;

  @Guard("foo")
  public boolean hello(String foo) {
    return false;
  }

  @TestStep("foo")
  public void epixx() {
  }

  @EndCondition
  public boolean ending(String foo) {
    return false;
  }

  @CoverageValue("my-state")
  public String noArgument() {
    return "";
  }

  @LastStep
  public boolean last(String noargs) {
    return false;
  }

}
