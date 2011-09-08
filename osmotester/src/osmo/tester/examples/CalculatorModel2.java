package osmo.tester.examples;

import osmo.tester.OSMOTester;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Transition;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.Length;

import java.io.PrintStream;

/**
 * This is the simple OSMO tester example model made by finite-state-machine (FSM) style
 *
 * This model is describing behavior of simple calculator.
 * The calculator can increase and decrease the count.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class CalculatorModel2 {
  private int counter = 0;
  private int testCount = 1;
  private State currentState;
  private final PrintStream out;
  private enum State{
	  Start,
	  Decrease,
	  Increase,
  }

  public CalculatorModel2() {
    this.out = System.out;
  }

  public CalculatorModel2(PrintStream out) {
    this.out = out;
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
    out.println("Starting new test case "+testCount);
    testCount++;
    currentState = null;
  }

  @AfterTest
  public void end() {
    out.println("Test case ended");
  }

  @Guard("start")
  public boolean checkStart() {
    return currentState == null;
  }

  @Transition("start")
  public void startState() {
    out.println("S:" + counter);
    counter++;
    currentState = State.Start;
  }

  @Guard("decrease")
  public boolean DecreaseGuard() {
    return currentState == State.Increase || currentState == State.Decrease || currentState == State.Start;
  }

  @Transition("decrease")
  public void decreaseState() {
	out.println("Decreased: " + counter--);
    currentState = State.Decrease;
  }

  @Guard("increase")
  public boolean IncreaseGuard() {
	    return currentState == State.Increase || currentState == State.Decrease || currentState == State.Start;
  }

  @Transition("increase")
  public void increaseState() {
	out.println("Increased: " + counter++);
    currentState = State.Increase;
  }

  public static void main2(String[] args) {
    OSMOTester tester = new OSMOTester(new CalculatorModel2());
    tester.generate();
  }

  /**
   * Shows an example of configuring the generator.
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester(new CalculatorModel2());
    tester.setAlgorithm(new RandomAlgorithm());
    tester.setDebug(true);
    tester.addSuiteEndCondition(new Length(10));
    tester.addTestEndCondition(new Length(10));
    tester.generate();
  }
}
