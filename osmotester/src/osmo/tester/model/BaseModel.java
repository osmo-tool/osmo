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
 * The provided fields are initialized with @BeforeTest, meaning that any use of this class cannot rely in
 * other @BeforeTest annotations that these fields are initialized.
 * An exception is the "previous" field, which is initialized with @Post, meaning that any use of this class
 * cannot rely in other @Post annotations that this field is initialized.
 *
 * @author Teemu Kanstren
 */
public class BaseModel {
  /** Current test case being generated. */
  protected TestCase test = null;
  /** The suite being generated. */
  @TestSuiteField
  public TestSuite suite = null;
  /** For defining requirements. */
  @RequirementsField
  protected Requirements req = new Requirements();
  /** Previous generated test case. */
  protected TestStep previous = null;
  /** Id of the currently generated test case. */
  protected int id = -1;

  /**
   * Initializes the relevant fields for a new test case.
   * Note that other @BeforeTest annotations cannot rely on these fields as they are only initialized here.
   */
  @BeforeTest
  public void initTest() {
    test = suite.getCurrent();
    id = test.getId();
    previous = null;
  }

  /**
   * Initializes the previous field to reflect the correct status.
   * Note that due to initialization here, other @Post annotations cannot rely on the previous field.
   */
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

  /**
   * Marks the current test case as failed. Mostly only useful for reporting in online tests.
   */
  public void failed() {
    test.fail();
  }

  /**
   * Tells if the current test has been marked as failed or not.
   *
   * @return True if the test has been marked as failed.
   */
  public boolean isFailed() {
    return !test.isSuccess();
  }

  /**
   * This is to allow the user to store the test script for whatever purposes they like.
   * The script is not used by OSMOTester but it can be useful to store this and other properties
   * in case later an optimizer is run to modify the test suite ordering etc. and the script is not
   * optimal to store at the moment of creation.
   *
   * @param script New script for this test case.
   */
  public void setScript(String script) {
    test.setScript(script);
  }

  /**
   * Sets given property for test case to given value. This is to give the user a chance to store any
   * properties they like and do with the what they like. They are not used by OSMOTester itself.
   *
   * @param key Identifier for property.
   * @param value The new value for the property.
   */
  public void setProperty(String key, Object value) {
    test.setProperty(key, value);
  }
}
