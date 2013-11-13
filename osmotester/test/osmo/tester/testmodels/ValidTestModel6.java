package osmo.tester.testmodels;

import osmo.tester.annotation.LastStep;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.Transition;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;

import static junit.framework.Assert.assertEquals;

/** @author Teemu Kanstren */
public class ValidTestModel6 {
  private TestSuite history = null;
  private String states = "";
  @Variable
  private int index = 0;
  private final String expectedState;
  private boolean nocheck = false;

  public ValidTestModel6() {
    this.expectedState = null;
  }

  public ValidTestModel6(String expectedState) {
    this.expectedState = expectedState;
  }
  
  public void disableCheck() {
    nocheck = true;
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
    states += ":"+history.getCurrentTest().getCurrentStep().getValuesFor("my-state")+":";
  }

  @Post("all")
  public void savePostState() {
    states += "-"+history.getCurrentTest().getCurrentStep().getValuesFor("my-state")+"-";
  }

  public String getStates() {
    return states;
  }
  
  @LastStep
  public void check() {
    if (nocheck) return;
    assertEquals("Expected result", expectedState, states);
  }
}
