package osmo.tester.examples.calculator;

import osmo.tester.OSMOTester;
import osmo.tester.annotation.*;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.Length;

import java.io.PrintStream;

/**
 * This is the simple OSMO tester example model made by finite-state-machine (FSM) style
 * <p/>
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

  private enum State {
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
    out.println("Starting new test case " + testCount);
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

  @TestStep("start")
  public void startState() {
    out.println("S:" + counter);
    counter++;
    currentState = State.Start;
  }

  @Guard("decrease")
  public boolean DecreaseGuard() {
    return currentState == State.Increase || currentState == State.Decrease || currentState == State.Start;
  }

  @TestStep("decrease")
  public void decreaseState() {
    out.println("Decreased: " + counter--);
    currentState = State.Decrease;
  }

  @Guard("increase")
  public boolean IncreaseGuard() {
    return currentState == State.Increase || currentState == State.Decrease || currentState == State.Start;
  }

  @TestStep("increase")
  public void increaseState() {
    out.println("Increased: " + counter++);
    currentState = State.Increase;
  }

  public static void main2(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel2.class));
    tester.generate(55);
  }

  /**
   * Shows an example of configuring the generator.
   *
   * @param args not used.
   */
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel2.class));
    tester.setAlgorithm(new RandomAlgorithm());
    tester.setSuiteEndCondition(new Length(10));
    tester.setTestEndCondition(new Length(10));
    tester.generate(55);
  }
}
