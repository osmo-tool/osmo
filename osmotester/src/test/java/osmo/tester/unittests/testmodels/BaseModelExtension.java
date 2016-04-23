package osmo.tester.unittests.testmodels;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.TestStep;
import osmo.tester.model.BaseModel;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class BaseModelExtension extends BaseModel {
  private boolean first = false;
  private boolean second = false;
  private static int count = -1;
  public int checkCount = 0;

  @BeforeTest
  public void initialization() {
    //init this here just once to allow adoption to previously generated tests
    if (count < 0) {
      count = suite.getCurrentTest().getId();
    } else {
      count++;
    }
    first = false;
    second = false;
  }

  @Guard("first")
  public boolean allowFirst() {
    return !first;
  }

  @TestStep("first")
  public void first() {
    assertNull("Previous should be null at first transition", previous);
    first = true;
  }

  @Guard("second")
  public boolean allowSecond() {
    return first;
  }

  @Pre("second")
  public void beforeSecond() {
  }

  @TestStep("second")
  public void second() {
    String transition = previous.getName();
    assertNotNull("Previous transition", transition);
    assertEquals("Previous transition", "first", transition);
    second = true;
  }

  @Post("all")
  public void checkMe() {
    checkCount++;
  }

  @EndCondition
  public boolean endNow() {
    return first && second;
  }
}
