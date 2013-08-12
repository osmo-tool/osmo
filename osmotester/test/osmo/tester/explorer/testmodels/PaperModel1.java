package osmo.tester.explorer.testmodels;

import osmo.common.NullPrintStream;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.explorer.OSMOExplorer;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.ExplorationEnabler;
import osmo.tester.annotation.GenerationEnabler;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;

import java.io.PrintStream;

/**
 * Same as the other calculator but without explicit state enumeration. Instead this will keep the counter > 0 and
 * use the counter itself to define the state. This also illustrates how you can name your methods and elements in
 * any way you like, since only the annotations are used.
 *
 * @author Teemu Kanstren
 */
public class PaperModel1 {
  private static int steps = 0;
  @Variable
  private int counter = 0;
  @TestSuiteField
  private TestSuite suite = null;
  private PrintStream out = null;
  private final PrintStream real;

  public PaperModel1(PrintStream real) {
    this.real = real;
  }

  @ExplorationEnabler
  public void enableExploration() {
    out = NullPrintStream.stream;
  }

  @GenerationEnabler
  public void enableGeneration() {
    out = real;
  }

  @BeforeTest
  public void start() {
    counter = 0;
    out.println("Starting new test case " + suite.getAllTestCases().size());
  }

  @Guard("decrease")
  public boolean toDecreaseOrNot() {
    return counter > 1;
  }

  @TestStep("decrease")
  public void decreaseState() {
//    sleep();
    counter--;
    out.print("-");
//    out.println("-"+counter);
    if (out == System.out) {
      steps++;
    }
  }
  
  private void sleep() {
    if (out != System.out) {
      return;
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @TestStep("increase")
  public void increaseState() {
//    sleep();
    counter++;
//    out.println("+"+counter);
    out.print("+");
    if (out == System.out) {
      steps++;
    }
  }

  public static void main(String[] args) {
    OSMOExplorer osmo = new OSMOExplorer();
//    osmo.setSeed(55);
    ExplorationConfiguration config = createConfiguration();
    long start = System.currentTimeMillis();
    osmo.explore(config);
    long end = System.currentTimeMillis();
    long diff = end-start;
    System.out.println("diff:" + diff);
    System.out.println("steps:"+steps);
  }

  private static ExplorationConfiguration createConfiguration() {
    PaperModel1Factory factory = new PaperModel1Factory(System.out);
    ExplorationConfiguration config = new ExplorationConfiguration(factory, 5, 55);
    config.setFallbackProbability(1d);
    config.setMinTestLength(10);
    config.setLengthWeight(0);
    config.setRequirementWeight(0);
    config.setDefaultValueWeight(1);
    config.setStepWeight(0);
    config.setStepPairWeight(10);
    config.setVariableCountWeight(0);
    config.setMinTestScore(60);
    config.setMinSuiteLength(5);
    config.setTestPlateauThreshold(1);
    return config;
  }
}
