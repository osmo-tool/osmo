package osmo.tester.endconditions;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generation.TestSequenceListener;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.structure.StepCoverage;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.TransitionName;
import osmo.tester.testmodels.VariableModel2;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class StepsCoverageTests {
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
  private StepCoverage stepCoverage;

  @Before
  public void setup() {
    fsm = new FSM();
    t1 = createTransition("t1", 1);
    t2 = createTransition("t2", 1);
    t3 = createTransition("t3", 1);
    t4 = createTransition("t4", 1);
    t5 = createTransition("t5", 1);
    t6 = createTransition("t6", 1);
    t7 = createTransition("t7", 1);
    t8 = createTransition("t8", 1);
    t9 = createTransition("t9", 1);
    t10 = createTransition("t10", 1);
    suite = new TestSuite();
    suite.init();
    suite.startTest();
    stepCoverage = new StepCoverage();
  }

  private FSMTransition createTransition(String name, int weight) {
    return fsm.createTransition(new TransitionName("", name), weight);
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

  @Test
  public void nonExistentStep() {
    VariableModel2 model = new VariableModel2();
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(model);
    StepCoverage sc = new StepCoverage();
    sc.addRequiredStep("non-existent");
    Length length1 = new Length(1);
    osmo.setTestEndCondition(sc);
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate();
      fail("Generation with coverage for non-existent test step (transition) should fail.");
    } catch (IllegalStateException e) {
      //Expected
      assertEquals("Reported error", "Impossible coverage requirements, defined steps [non-existent] not found.", e.getMessage());
    }
  }

  @Test
  public void stepSeveralTimesWithGeneration() {
    VariableModel2 model = new VariableModel2();
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(model);
    StepCoverage sc = new StepCoverage();
    sc.addRequiredStep("third");
    sc.addRequiredStep("third");
    Length length1 = new Length(1);
    osmo.setTestEndCondition(sc);
    osmo.setSuiteEndCondition(length1);
    TestSequenceListener listener = new TestSequenceListener(false);
    listener.addExpected("suite-start", "start", "t:first", "t:second", "t:third", "t:third", "end", "suite-end");
    osmo.addListener(listener);
    osmo.generate();
    listener.validate("Step coverage with two 'third' steps required.");
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
