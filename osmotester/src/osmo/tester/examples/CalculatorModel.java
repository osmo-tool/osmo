package osmo.tester.examples;

import osmo.tester.OSMOTester;
import osmo.tester.annotation.After;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.Before;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.endcondition.LengthCondition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.Requirements;

import java.io.PrintStream;

/**
 * Same as the other calculator but without explicit state enumeration. Instead this will keep the counter > 0 and
 * use the counter itself to define the state. This also illustrates how you can name your methods and elements in
 * any way you like, since only the annotations are used.
 * 
 * @author Teemu Kanstren
 */
public class CalculatorModel {
  @RequirementsField
  private Requirements requirement = new Requirements();
  @TestSuiteField
  private TestSuite history = null;
  private int counter = 0;
  private int testCount = 1;
  private static final String REQ_INCREASE = "increase";
  private static final String REQ_DECREASE = "decrease";
  private final PrintStream out;

  public CalculatorModel() {
    this(System.out);
  }

  public CalculatorModel(PrintStream out) {
    requirement.add(REQ_INCREASE);
    requirement.add(REQ_DECREASE);
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

  @Before
  public void start() {
    counter = 0;
    out.println("Starting new test case "+testCount);
    testCount++;
  }

  @After
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
    requirement.covered(REQ_DECREASE);
    counter--;
    out.println("- " + counter);
  }

  @Guard("increase")
  public boolean shallWeIncrease() {
    return counter > 0;
  }

  @Transition("increase")
  public void increaseState() {
    requirement.covered(REQ_INCREASE);
    counter++;
    out.println("+ " + counter);
  }

  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new CalculatorModel());
    tester.addSuiteEndCondition(new LengthCondition(100));
    tester.generate();
  }
}
