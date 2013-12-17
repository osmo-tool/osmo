package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.CoverageValue;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.ValueRange;

/** @author Teemu Kanstren */
public class CoverageValueModel2 {
  private TestSuite history = null;
  private String state = "start";
  @Variable
  private ValueRange<Integer> range = new ValueRange<>(1, 5);

  @TestStep("t1")
  public void one() {
    state = "1";
//    range.next();
  }

  @TestStep("t2")
  public void two() {
    state = "2";
//    range.next();
  }

  @TestStep("t3")
  public void three() {
    state = "3";
    range.next();
  }

  @TestStep("t4")
  public void four() {
    state = "4";
//    range.next();
  }

  @CoverageValue("my-state")
  public String state(TestCaseStep step) {
    return state;
  }

  @Pre("all")
  public void savePreState() {
  }

  @Post("all")
  public void savePostState() {
  }

}
