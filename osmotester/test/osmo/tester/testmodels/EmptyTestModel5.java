package osmo.tester.testmodels;

import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Transition;

/** @author Teemu Kanstren */
public class EmptyTestModel5 {
  @Guard("foo")
  public boolean hello() {
    return false;
  }

  @TestStep("foo")
  public void epixx(String bar) {
  }

  @EndCondition
  public String hello(String foo) {
    return "";
  }
}
