package osmo.tester.unittests.explorer.testmodels;

import osmo.tester.annotation.*;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

import java.io.PrintStream;

/**
 * Same as {@link osmo.tester.unittests.testmodels.CalculatorModel} but without exploration enablers.
 *
 * @author Teemu Kanstren
 */
public class CalculatorModelNoExploration {
  private Requirements req = new Requirements();
  private TestSuite history = null;
  @Variable
  private int counter = 0;
  private int testCount = 1;
  private static final String REQ_INCREASE = "increase";
  private static final String REQ_DECREASE = "decrease";
  private PrintStream out;

  public CalculatorModelNoExploration() {
    this(null);
  }

  public CalculatorModelNoExploration(PrintStream out) {
    req.add(REQ_INCREASE);
    req.add(REQ_DECREASE);
    this.out = out;
  }

  public TestSuite getHistory() {
    return history;
  }

  @BeforeSuite
  public void first() {
    out.println("first");
  }

  @AfterSuite
  public void last() {
    out.println("last");
  }

  @BeforeTest
  public void start() {
    counter = 0;
    out.println("Starting new test case " + testCount);
    testCount++;
  }

  @AfterTest
  public void end() {
    out.println("Test case ended");
  }

  @Guard("start")
  public boolean checkStart() {
    return counter == 0;
  }

  @TestStep("start")
  public void startState() {
    out.println("S:" + counter);
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
    out.println("- " + counter);
  }

  @Guard("increase")
  public boolean shallWeIncrease() {
    return counter > 0;
  }

  @TestStep("increase")
  public void increaseState() {
    req.covered(REQ_INCREASE);
    counter++;
    out.println("+ " + counter);
  }
}
