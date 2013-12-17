package osmo.tester.unittests.testmodels;

import osmo.common.NullPrintStream;
import osmo.tester.OSMOTester;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.ExplorationEnabler;
import osmo.tester.annotation.GenerationEnabler;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.Length;
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
  private final Requirements req = new Requirements();
  private TestSuite history = null;
  @Variable
  private int counter = 0;
  private static final String REQ_INCREASE = "increase";
  private static final String REQ_DECREASE = "decrease";
  private PrintStream out;
  private int sleepTime = 0;

  public CalculatorModel() {
    this(null);
  }

  public CalculatorModel(PrintStream out) {
    req.add(REQ_INCREASE);
    req.add(REQ_DECREASE);
    this.out = out;
  }

  public CalculatorModel(int sleepTime) {
    this(null);
    this.sleepTime = sleepTime;
  }

  public TestSuite getHistory() {
    return history;
  }

  @BeforeSuite
  public void first() {
    //stupid hack to capture the stream set in system.out by jenkinsreport that uses this model.. hacks on hacks and stuff dude
    if (out != NullPrintStream.stream) {
      out = System.out;
    }
    out.println("first");
  }

  @ExplorationEnabler
  public void enableExploration() {
//    if (out == null) {out = NullPrintStream.stream;}
    out = NullPrintStream.stream;
  }

  @GenerationEnabler
  public void enableGeneration() {
//    if (out == null) { out = System.out;}
    out = System.out;
  }

  @AfterSuite
  public void last() {
    out.println("last");
  }

  @BeforeTest
  public void start() {
    counter = 0;
    out.println("Starting new test case " + (history.getAllTestCases().size()));
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
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      //ignored
    }
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
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      //ignored
    }
    req.covered(REQ_INCREASE);
    counter++;
    out.println("+ " + counter);
  }

  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    tester.setAlgorithm(new RandomAlgorithm());
    tester.setTestEndCondition(new Length(100));
    tester.setSuiteEndCondition(new Length(100));
    tester.generate(333);
  }
}
