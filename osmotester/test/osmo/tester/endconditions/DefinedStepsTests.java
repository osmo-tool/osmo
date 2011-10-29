package osmo.tester.endconditions;

import org.junit.Before;
import org.junit.Test;
import osmo.common.log.Logger;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calculator.CalculatorModel;
import osmo.tester.generator.endcondition.DefinedStepCoverage;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class DefinedStepsTests {
  private FSM fsm;
  private FSMTransition t1;
  private FSMTransition t2;
  private FSMTransition t3;
  private FSMTransition t4;
  private FSMTransition t5;
  private FSMTransition t6;
  private FSMTransition t7;
  private FSMTransition t8;
  private FSMTransition t9;
  private FSMTransition t10;
  private TestSuite suite;
  private DefinedStepCoverage stepCoverage;

  @Before
  public void setup() {
    fsm = new FSM();
    t1 = fsm.createTransition("t1", 1);
    t2 = fsm.createTransition("t2", 1);
    t3 = fsm.createTransition("t3", 1);
    t4 = fsm.createTransition("t4", 1);
    t5 = fsm.createTransition("t5", 1);
    t6 = fsm.createTransition("t6", 1);
    t7 = fsm.createTransition("t7", 1);
    t8 = fsm.createTransition("t8", 1);
    t9 = fsm.createTransition("t9", 1);
    t10 = fsm.createTransition("t10", 1);
    suite = new TestSuite();
    suite.startTest();
    stepCoverage = new DefinedStepCoverage();
  }

  @Test
  public void nothingToCover() {
    String msg1 = "No steps to cover end condition for test should be always true";
    String msg2 = "No steps to cover end condition for suite should be always true";
    assertEnd(msg1, msg2);

    suite.addStep(t1);
    suite.endTest();
    suite.startTest();
    suite.addStep(t1);
    suite.addStep(t2);
    assertEnd(msg1, msg2);
  }

  @Test
  public void oneToCover() {
    stepCoverage.addRequiredStep("t1");
    String msg1 = "Return value for test end condition when required steps are covered";
    String msg2 = "Return value for test end condition when required steps are covered";
    assertNoEnd(msg1, msg2);

    suite.addStep(t1);
    assertEnd(msg1, msg2);
    suite.endTest();
    suite.startTest();
    suite.addStep(t1);
    suite.addStep(t2);
    assertEnd(msg1, msg2);
  }

  @Test
  public void twoToCover() {
    stepCoverage.addRequiredStep("t1");
    stepCoverage.addRequiredStep("t2");
    String msg1 = "Return value for test end condition when required steps are covered";
    String msg2 = "Return value for test end condition when required steps are covered";
    assertNoEnd(msg1, msg2);

    suite.addStep(t1);
    assertNoEnd(msg1, msg2);
    suite.endTest();
    suite.startTest();
    suite.addStep(t2);
    assertNoTestEnd(msg1);
    assertSuiteEnd(msg2);
    suite.addStep(t1);
    assertEnd(msg1, msg2);
  }

  @Test
  public void fiveToCover() {
    stepCoverage.addRequiredStep("t1");
    stepCoverage.addRequiredStep("t2");
    stepCoverage.addRequiredStep("t3");
    stepCoverage.addRequiredStep("t4");
    stepCoverage.addRequiredStep("t5");
    String msg1 = "Return value for test end condition when required steps are covered";
    String msg2 = "Return value for test end condition when required steps are covered";
    assertNoEnd(msg1, msg2);

    suite.addStep(t1);
    assertNoEnd(msg1, msg2);
    suite.endTest();
    suite.startTest();
    suite.addStep(t2);
    suite.addStep(t3);
    suite.addStep(t4);
    assertNoEnd(msg1, msg2);
    suite.addStep(t5);
    assertNoTestEnd(msg1);
    assertSuiteEnd(msg2);
    suite.addStep(t1);
    assertEnd(msg1, msg2);
  }

  @Test
  public void fiveToCoverWithRepeats() {
    stepCoverage.addRequiredStep("t1");
    stepCoverage.addRequiredStep("t2");
    stepCoverage.addRequiredStep("t3");
    stepCoverage.addRequiredStep("t2");
    stepCoverage.addRequiredStep("t4");
    stepCoverage.addRequiredStep("t5");
    stepCoverage.addRequiredStep("t4");
    stepCoverage.addRequiredStep("t4");
    String msg1 = "Return value for test end condition when required steps are covered";
    String msg2 = "Return value for test end condition when required steps are covered";
    assertNoEnd(msg1, msg2);

    suite.addStep(t1);
    assertNoEnd(msg1, msg2);
    suite.endTest();
    suite.startTest();
    suite.addStep(t2);
    suite.addStep(t3);
    suite.addStep(t4);
    assertNoEnd(msg1, msg2);
    suite.addStep(t5);
    assertNoEnd(msg1, msg2);
    suite.endTest();
    suite.startTest();
    suite.addStep(t1);
    suite.addStep(t2);
    suite.addStep(t2);
    suite.addStep(t1);
    suite.addStep(t4);
    assertNoEnd(msg1, msg2);
    suite.addStep(t4);
    assertNoTestEnd(msg1);
    assertSuiteEnd(msg2);
    suite.addStep(t3);
    suite.addStep(t4);
    suite.addStep(t3);
    suite.addStep(t5);
    assertEnd(msg1, msg2);
  }

  private void assertNoSuiteEnd(String msg) {
    boolean end = stepCoverage.endSuite(suite, fsm);
    assertEquals(msg, false, end);
  }

  private void assertNoTestEnd(String msg) {
    boolean end = stepCoverage.endTest(suite, fsm);
    assertEquals(msg, false, end);
  }


  private void assertSuiteEnd(String msg) {
    boolean end = stepCoverage.endSuite(suite, fsm);
    assertEquals(msg, true, end);
  }

  private void assertTestEnd(String msg) {
    boolean end = stepCoverage.endTest(suite, fsm);
    assertEquals(msg, true, end);
  }


  private void assertNoEnd(String msg1, String msg2) {
    assertNoTestEnd(msg1);
    assertNoSuiteEnd(msg2);
  }

  private void assertEnd(String msg1, String msg2) {
    assertTestEnd(msg1);
    assertSuiteEnd(msg2);
  }
}
