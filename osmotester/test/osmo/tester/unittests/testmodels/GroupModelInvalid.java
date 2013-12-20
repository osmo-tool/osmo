package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.Group;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;

/** @author Teemu Kanstren */
@Group()
public class GroupModelInvalid {
  @TestStep(name="step1", group="")
  public void groupedStep() {
    
  }

  @TestStep(name="step2", group="step3")
  public void groupedTransition() {

  }

  @TestStep(name="step3")
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

  @Pre("group1")
  public boolean group1Pre() {
    return false;
  }

  @Post("group1")
  public boolean group1Post() {
    return false;
  }
  
  @Pre("all")
  public void ohnoes() {
  }

  @Post("all")
  public void ohnoes2() {
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
