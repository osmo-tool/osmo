package osmo.tester.testmodels;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Transition;

/** @author Teemu Kanstren */
public class GroupModel1 {
  @TestStep(name="step1", group="group1")
  public void groupedStep() {
    
  }

  @Transition(name="step2", group="group1")
  public void groupedTransition() {

  }

  @TestStep(name="step3", group="group1")
  public void groupedStep3() {

  }

  @TestStep("step4")
  public void step4() {

  }
  
  @Guard
  public boolean allGuard() {
    return false;
  }

  @Guard("group1")
  public boolean group1Guard() {
    return false;
  }

  @Guard("step3")
  public boolean step3Guard() {
    return false;
  }

  @Guard("step4")
  public boolean step4Guard() {
    return false;
  }
}
