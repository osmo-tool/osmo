package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.LastStep;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.Transition;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;

/** @author Teemu Kanstren */
public class ValidTestModel6 {
  private TestSuite history = null;
  @Variable
  private int index = 0;

  public ValidTestModel6() {
  }

  @Transition("t1")
  public void one() {
    index = 1;
  }

  @Transition("t2")
  public void two() {
    index = 2;
  }

  @Transition("t3")
  public void three() {
    index = 3;
  }

  @Transition("t4")
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
