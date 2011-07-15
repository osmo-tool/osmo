package osmo.tester.model;

import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.List;

/**
 * A simple base class for providing some common functionality for test models.
 * You can either extend this for your models or just use the annotations in any other class.
 *
 * @author Teemu Kanstren
 */
public class BaseModel {
  protected TestCase test = null;
  @TestSuiteField
  public TestSuite suite = null;
  @RequirementsField
  protected Requirements req = new Requirements();
  protected TestStep previous = null;
  protected int id = -1;

  @BeforeTest
  public void initTest() {
    test = suite.getCurrent();
    id = test.getId();
    previous = null;
  }

  @Post
  public void afterTransition() {
    List<TestStep> steps = test.getSteps();
    int size = steps.size();
    if (size < 1) {
      return;
    }
    //set this transition as the "previous" one, which means of course that no @Post can rely on this
    previous = steps.get(size-1);
  }

  public void failed() {
    test.fail();
  }

  public boolean isFailed() {
    return !test.isSuccess();
  }

  public void saveScript(String script) {
    test.setScript(script);
  }

  public void setProperty(String key, Object value) {
    test.setProperty(key, value);
  }
}
