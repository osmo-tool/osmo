package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.LastStep;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.ToStringValue;

/** @author Teemu Kanstren */
public class ValidTestModel6 {
  @Variable
  private int index = 0;

  public ValidTestModel6() {
  }

  @TestStep("t1")
  public void one() {
    index = 1;
  }

  @TestStep("t2")
  public void two() {
    index = 2;
  }

  @TestStep("t3")
  public void three() {
    index = 3;
  }

  @TestStep("t4")
  public void four() {
    index = 4;
  }

  @Pre("all")
  public void savePreState() {
  }

  @Post("all")
  public void savePostState() {
  }
  
  @LastStep
  public void check() {
  }
}
