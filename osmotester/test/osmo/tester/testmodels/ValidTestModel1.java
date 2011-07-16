package osmo.tester.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Transition;

/**
 * Test model that has no enabled transition that can be taken.
 *
 * @author Teemu Kanstren
 */
public class ValidTestModel1 {
  @Transition("world")
  public void epix() {

  }

  @Guard("world")
  public boolean listCheck() {
    return false;
  }

  @Transition("epixx")
  public void epixx() {

  }

  @Guard("epixx")
  public boolean kitted() {
    return false;
  }
}
