package osmo.tester.model;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Post;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.List;

/**
 * A base class for providing some common functionality for simple test models.
 * <p>
 * Has fields for current test step, test case, and test suite currently being generated.
 * Also includes a requirements field, which needs to be copied elsewhere if used in several model objects.
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
  /** Previous generated test step. */
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
  @Post("all")
  public void afterAll() {
    List<TestCaseStep> steps = test.getSteps();
    int size = steps.size();
    if (size < 1) {
      return;
    }
    //set this step as the "previous" one, which means of course that no @Post can rely on this
    previous = steps.get(size - 1);
  }
}
