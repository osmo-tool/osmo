package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.TestStep;
import osmo.tester.model.Requirements;

/**
 * @author Teemu Kanstren
 */
public class Model10 {
  private final Requirements req;
  private int x = 0;
  private int y = 0;

  public Model10(Requirements req) {
    this.req = req;
  }

  @TestStep
  public void step1() {
    x++;
    if (x == 10) req.covered("X10");
  }

  @TestStep
  public void step2() {
  }

  @TestStep
  public void step3() {
  }

  @TestStep
  public void step4() {
  }

  @TestStep
  public void step5() {
    x++;
    y++;
    if (x == 3) req.covered("X3");
  }

  @TestStep
  public void step6() {
    y++;
  }

  @TestStep
  public void step7() {
    if (y == 10) req.covered("Y10");
  }

  @TestStep
  public void step8() {
    //this is difficult to cover as anything above 5 for either will fail
    if (x == 5 && y == 5) req.covered("X&Y 5");
  }

  @TestStep
  public void step9() {
  }

  @TestStep
  public void step10() {
  }
}
