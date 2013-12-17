package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.Group;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
@Group("big-group")
public class GroupModel1 {
  @TestStep(name="step1", group="group1")
  public void groupedStep() {
    
  }

  @TestStep(name="step2", group="group1")
  public void groupedTransition() {

  }

  @TestStep(name="step3", group="group1")
  public void groupedStep3() {

  }

  @TestStep("step4")
  public void step4() {

  }
  
  @Guard("all")
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
  
  @Guard("big-group")
  public boolean classGuard() {
    return false;
  }

  @Pre
  public void ohnoes() {
  }

  @Post
  public void ohnoes2() {
  }
}
