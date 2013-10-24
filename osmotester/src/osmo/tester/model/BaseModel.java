package osmo.tester.model;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Post;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.List;

/**
 * A simple base class for providing some common functionality for test models.
 * You can either extend this for your models or just use the annotations in any other class.
 * <p/>
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
  public TestSuite suite = null;
  /** For defining requirements. */
  protected Requirements req = new Requirements();
  /** Previous generated test case. */
  protected TestCaseStep previous = null;
  /** Id of the currently generated test case. */
  protected int id = -1;

  /**
   * Initializes the relevant fields for a new test case.
   * Note that other @BeforeTest annotations cannot rely on these fields as they are only initialized here.
   */
  @BeforeTest
  public void initTest() {
    test = suite.getCurrentTest();
    id = test.getId();
    previous = null;
  }

  /**
   * Initializes the previous field to reflect the correct status.
   * Note that due to initialization here, other @Post annotations cannot rely on the previous field.
   */
  @Post
  public void afterTransition() {
    List<TestCaseStep> steps = test.getSteps();
    int size = steps.size();
    if (size < 1) {
      return;
    }
    //set this transition as the "previous" one, which means of course that no @Post can rely on this
    previous = steps.get(size - 1);
  }
}
