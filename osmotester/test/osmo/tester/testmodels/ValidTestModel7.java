package osmo.tester.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;

import java.io.PrintStream;

/**
 * This model runs steps in an order where the lest covered is not always enabled.
 *
 * @author Teemu Kanstren
 */
public class ValidTestModel7 {
  private int count1 = 0;
  private int count2 = 0;
  private int count3 = 0;
  private int count4 = 0;
  private final PrintStream out;

  //TODO: remove this constructor and add checks
  public ValidTestModel7() {
    out = null;
  }

  public ValidTestModel7(PrintStream out) {
    this.out = out;
  }

  //todo: give means for guard to access information on which step is being guarded
  @Guard("t1")
  public boolean allowOne() {
    if (count1 < 10) {
      return true;
    }
    return count2 > 20;
  }

  @Guard("t4")
  public boolean allowFour() {
    if (count4 < 10) {
      return true;
    }
    return count2 > 20;
  }

  @Guard("t3")
  public boolean allowThree() {
    if (count3 < 10) {
      return true;
    }
    return count2 > 20;
  }

  @TestStep("t1")
  public void one() {
    System.out.println("T1");
    count1++;
  }

  @TestStep("t2")
  public void two() {
    System.out.println("T2");
    count2++;
  }

  @TestStep("t3")
  public void three() {
    System.out.println("T3");
    count3++;
  }

  @TestStep("t4")
  public void four() {
    System.out.println("T4");
    count4++;
  }
}
