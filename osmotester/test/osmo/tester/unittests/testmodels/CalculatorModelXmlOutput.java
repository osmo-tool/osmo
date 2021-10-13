package osmo.tester.unittests.testmodels;

import osmo.common.NullPrintStream;
import osmo.tester.OSMOTester;
import osmo.tester.annotation.*;
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
public class CalculatorModelXmlOutput {
  private final Requirements req = new Requirements();
  private TestSuite history = null;
  @Variable
  private int counter = 0;
  private static final String REQ_INCREASE = "increase";
  private static final String REQ_DECREASE = "decrease";
  private PrintStream out;
  private int sleepTime = 0;

  public CalculatorModelXmlOutput() {
    this(null);
  }

  public CalculatorModelXmlOutput(PrintStream out) {
    req.add(REQ_INCREASE);
    req.add(REQ_DECREASE);
    this.out = out;
  }

  public CalculatorModelXmlOutput(int sleepTime) {
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
    out.println("<suite>");
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
    out.println("</suite>");
  }

  @BeforeTest
  public void start() {
    counter = 0;
    out.println("<test><id>" + (history.getAllTestCases().size()) + "</id>");
  }

  @AfterTest
  public void end() {
    out.println("</test>");
  }

  @Guard("start")
  public boolean checkStart() {
    return counter == 0;
  }

  @TestStep("start")
  public void startState() {
    out.println("<start>" + counter + "</start>");
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
    out.println("<sub>" + counter + "</sub>");
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
    out.println("<add>" + counter + "</add>");
  }

  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModelXmlOutput.class));
    tester.setAlgorithm(new RandomAlgorithm());
    tester.setTestEndCondition(new Length(100));
    tester.setSuiteEndCondition(new Length(100));
    tester.generate(333);
  }
}
