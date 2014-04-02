package osmo.tester.unittests.explorer.testmodels;

import osmo.common.NullPrintStream;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Transition;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

import java.io.PrintStream;

/**
 * Same as {@link osmo.tester.unittests.testmodels.CalculatorModel} but without exploration enablers.
 *
 * @author Teemu Kanstren
 */
public class CalculatorModelNoPrints {
  private Requirements req = new Requirements();
  private TestSuite history = null;
  @Variable
  private int counter = 0;
  private int testCount = 1;
  private static final String REQ_INCREASE = "increase";
  private static final String REQ_DECREASE = "decrease";
  private PrintStream out = NullPrintStream.stream;

  public CalculatorModelNoPrints() {
    req.add(REQ_INCREASE);
    req.add(REQ_DECREASE);
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

  @Transition("start")
  public void startState() {
    out.println("S:" + counter);
    counter++;
  }

  @Guard("decrease")
  public boolean toDecreaseOrNot() {
    return counter > 1;
  }

  @Transition("decrease")
  public void decreaseState() {
    req.covered(REQ_DECREASE);
    counter--;
    out.println("- " + counter);
  }

  @Guard("increase")
  public boolean shallWeIncrease() {
    return counter > 0;
  }

  @Transition("increase")
  public void increaseState() {
    req.covered(REQ_INCREASE);
    counter++;
    out.println("+ " + counter);
  }
}
