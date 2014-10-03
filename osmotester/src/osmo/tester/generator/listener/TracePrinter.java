package osmo.tester.generator.listener;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.io.PrintStream;

/** 
 * Prints a trace of test generation on the given output stream, defaults to System.out.
 * Currently prints only taken steps and errors observed.
 * 
 * @author Teemu Kanstren 
 */
public class TracePrinter implements GenerationListener {
  private PrintStream out = System.out;
  /** Number of test case running, used to print indices on trace. */
  private int testIndex = 1;
  /** Number of test step running, used to print indices on trace. */
  private int stepIndex = 1;
  
  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void step(TestCaseStep step) {
    String name = step.getName();
    out.println(testIndex+"."+stepIndex+".STEP:"+name.toUpperCase());
    stepIndex++;
  }

  @Override
  public void lastStep(String name) {
    out.println(testIndex+"."+stepIndex+".LASTSTEP:"+name.toUpperCase());
    stepIndex++;
  }

  @Override
  public void pre(FSMTransition transition) {
  }

  @Override
  public void post(FSMTransition transition) {
  }

  @Override
  public void testStarted(TestCase test) {
  }

  @Override
  public void testEnded(TestCase test) {
    testIndex++;
    stepIndex = 1;
  }

  @Override
  public void testError(TestCase test, Throwable error) {
    TestCaseStep currentStep = test.getCurrentStep();
    String name = FSM.START_STEP_NAME;
    if (currentStep != null) name = currentStep.getName();
    out.println(testIndex+"."+stepIndex+".ERROR:"+name.toUpperCase());
    stepIndex++;
  }

  @Override
  public void suiteStarted(TestSuite suite) {
  }

  @Override
  public void suiteEnded(TestSuite suite) {
  }
}
