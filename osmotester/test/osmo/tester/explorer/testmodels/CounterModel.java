package osmo.tester.explorer.testmodels;

import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

/**
 * Same as the other calculator but without explicit state enumeration. Instead this will keep the counter > 0 and
 * use the counter itself to define the state. This also illustrates how you can name your methods and elements in
 * any way you like, since only the annotations are used.
 *
 * @author Teemu Kanstren
 */
public class CounterModel {
  @RequirementsField
  private Requirements req = new Requirements();
  @TestSuiteField
  private TestSuite history = null;
  @Variable
  private int counter = 0;
  private int testCount = 1;
  private static final String REQ_INCREASE = "increase";
  private static final String REQ_DECREASE = "decrease";
  private String script = "";
  public static int decreases = 0;
  public static int increases = 0;

  public CounterModel() {
    req.add(REQ_INCREASE);
    req.add(REQ_DECREASE);
  }

  public TestSuite getHistory() {
    return history;
  }

  @BeforeSuite
  public void first() {
    script += "first";
  }

  @AfterSuite
  public void last() {
    script += "last";
  }

  @BeforeTest
  public void start() {
    counter = 0;
    script += "Starting new test case " + testCount+"\n";
    testCount++;
  }

  @AfterTest
  public void end() {
    script += "\nTest case ended\n";
    history.getCurrentTest().setAttribute("test-script", script);
  }

  @Guard("start")
  public boolean checkStart() {
    return counter == 0;
  }

  @TestStep("start")
  public void startState() {
    script += "S:" + counter;
    counter++;
  }

  @Guard("decrease")
  public boolean toDecreaseOrNot() {
    return counter > 1;
  }

  @TestStep("decrease")
  public void decreaseState() {
    req.covered(REQ_DECREASE);
    counter--;
    script += "- " + counter;
    decreases++;
  }

  @Guard("increase")
  public boolean shallWeIncrease() {
    return counter > 0;
  }

  @TestStep("increase")
  public void increaseState() {
    req.covered(REQ_INCREASE);
    counter++;
    script += "+ " + counter;
    increases++;
  }
}
