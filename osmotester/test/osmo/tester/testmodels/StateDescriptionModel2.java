package osmo.tester.testmodels;

import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.StateName;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.ValueRange;

/** @author Teemu Kanstren */
public class StateDescriptionModel2 {
  @TestSuiteField
  private TestSuite history = null;
  private String states = "";
  private String state = "start";
  @Variable
  private ValueRange<Integer> range = new ValueRange<>(1, 5);

  @Transition("t1")
  public void one() {
    state = "1";
//    range.next();
  }

  @Transition("t2")
  public void two() {
    state = "2";
//    range.next();
  }

  @Transition("t3")
  public void three() {
    state = "3";
    range.next();
  }

  @Transition("t4")
  public void four() {
    state = "4";
//    range.next();
  }

  @StateName
  public String state(osmo.tester.generator.testsuite.TestStep step) {
    return state;
  }

  @Pre
  public void savePreState() {
    states += ":"+history.getCurrentTest().getCurrentStep().getState()+":";
  }

  @Post
  public void savePostState() {
    states += "-"+history.getCurrentTest().getCurrentStep().getState()+"-";
  }

  public String getStates() {
    return states;
  }
}
