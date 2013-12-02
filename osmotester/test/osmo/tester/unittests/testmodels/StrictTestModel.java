package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Transition;

/** @author Teemu Kanstren */
public class StrictTestModel {
  private boolean t1 = false;
  private boolean t2 = false;
  
  @Guard("a non-strict one")
  public boolean g1() {
    return t1 && !t2;
  }

  @Transition(name = "a non-strict one", strict = false)
  public void t1() {
    t2 = true;
    throw new RuntimeException("t1 fail");
  }

  @Guard("a strict one")
  public boolean g2() {
    return t2;
  }

  @Transition(name = "a strict one", strict = true)
  public void t2() {
    throw new RuntimeException("t2 fail");
  }

  @Guard("a non-strict one 2")
  public boolean g4() {
    return !t1;
  }

  @TestStep(name = "a non-strict one 2", strict = false)
  public void t4() {
    t1 = true;
    throw new RuntimeException("t3 fail");
  }

  @Guard("default strictness")
  public boolean g3() {
    return false;
  }

  @TestStep(name = "default strictness")
  public void t3() {
    
  }

}
