package osmo.tester.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Transition;

/** @author Teemu Kanstren */
public class EmptyTestModel6 {
  @TestStep("hello")
  public String transition1(String foo) {
    return "";
  }

  @TestStep("world2")
  public void epix(String bar) {

  }

  @Guard("world")
  public String listCheck() {
    return "";
  }
}
