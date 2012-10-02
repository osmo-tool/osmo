package osmo.tester.testmodels;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.annotation.Transition;
import osmo.tester.model.BaseModel;
import osmo.tester.model.FSMTransition;

import java.util.Map;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class BaseModelExtension extends BaseModel {
  private boolean first = false;
  private boolean second = false;
  private static int count = -1;
  public int checkCount = 0;
  public boolean firstChecked = false;
  public boolean secondChecked = false;

  @BeforeTest
  public void initSuite() {
    //init this here just once to allow adoption to previously generated tests
    if (count < 0) {
      count = test.getId();
    } else {
      count++;
    }
    first = false;
    second = false;
    firstChecked = false;
    secondChecked = false;
  }

  @Guard("first")
  public boolean allowFirst() {
    return !first;
  }

  @Transition("first")
  public void first() {
    assertNull("Previous should be null at first transition", previous);
    first = true;
  }

  @Guard("second")
  public boolean allowSecond() {
    return first;
  }

  @Pre("second")
  public void beforeSecond(Map<String, Object> p) {
    assertEquals("Parameters should be empty in (first) @Pre.", 0, p.size());
    p.put("seconded", true);
  }

  @Transition("second")
  public void second() {
    FSMTransition transition = previous.getTransition();
    assertNotNull("Previous transition", transition);
    assertEquals("Previous transition", "first", transition.getStringName());
    second = true;
  }

  @Post
  public void checkMe(Map<String, Object> p) {
    assertEquals("Test id", count, id);
    checkCount++;
    if (p.size() == 0) {
      firstChecked = true;
    } else {
      //note, this may throw nullpointer if p.get() gives null (due to autoboxing)
      secondChecked = (Boolean) p.get("seconded");
    }
  }

  @EndCondition
  public boolean endNow() {
    return first && second;
  }
}
