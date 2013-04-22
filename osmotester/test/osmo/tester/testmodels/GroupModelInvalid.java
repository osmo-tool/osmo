package osmo.tester.testmodels;

import osmo.tester.annotation.Group;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Transition;

/** @author Teemu Kanstren */
@Group()
public class GroupModelInvalid {
  @TestStep(name="step1", group="")
  public void groupedStep() {
    
  }

  @Transition(name="step2", group="step3")
  public void groupedTransition() {

  }

  @TestStep(name="step3")
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

  @Pre("group1")
  public boolean group1Pre() {
    return false;
  }

  @Post("group1")
  public boolean group1Post() {
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
