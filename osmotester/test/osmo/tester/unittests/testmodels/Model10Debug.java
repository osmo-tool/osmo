package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.TestStep;
import osmo.tester.model.Requirements;

/**
 * @author Teemu Kanstren
 */
public class Model10Debug {
  private int x = 0;
  private int y = 0;

  @TestStep
  public void step1() {
    x++;
  }

  @TestStep
  public void step2() {
  }

  @TestStep
  public void step3() {
  }

  @TestStep
  public void step4() {
    x++;
  }

  @TestStep
  public void step5() {
  }

  @TestStep
  public void step6() {
    y++;
  }

  @TestStep
  public void step7() {
    y++;
  }

  @TestStep
  public void step8() {
//    System.out.println("x:"+x+" y:"+y);
    //this is difficult to cover as anything above 5 for either will fail
    if (x == 5 && y == 5) {
      throw new RuntimeException("Failure here");
    }
  }

  @TestStep
  public void step9() {
  }

  @TestStep
  public void step10() {
  }
}
