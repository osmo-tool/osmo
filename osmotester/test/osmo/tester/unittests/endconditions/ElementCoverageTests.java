package osmo.tester.unittests.endconditions;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.structure.ElementCoverage;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.TransitionName;
import osmo.tester.unittests.testmodels.CalculatorModel;
import osmo.tester.unittests.testmodels.VariableModel2;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class ElementCoverageTests {
  private FSM fsm;
  private FSMTransition t1;
  private FSMTransition t2;
  private FSMTransition t3;
  private FSMTransition t4;
  private FSMTransition t5;
  private TestSuite suite;
  private ElementCoverage elementCoverage;

  @Before
  public void setup() {
    fsm = new FSM();
    t1 = createTransition("t1", 1);
    t2 = createTransition("t2", 1);
    t3 = createTransition("t3", 1);
    t4 = createTransition("t4", 1);
    t5 = createTransition("t5", 1);
    suite = new TestSuite();
    suite.startTest(1);
  }

  private FSMTransition createTransition(String name, int weight) {
    return fsm.createTransition(new TransitionName("", name), weight);
  }

  @Test
  public void nothingToCover() {
    elementCoverage = new ElementCoverage(0, 0, 0);
    String msg1 = "Nothing to cover end condition for test should be always true";
    String msg2 = "Nothing to cover end condition for suite should be always true";
    assertEnd(msg1, msg2);

    suite.addStep(t1);
    suite.endTest();
    suite.startTest(1);
    suite.addStep(t1);
    suite.addStep(t2);
    assertEnd(msg1, msg2);
  }

  @Test
  public void oneStepToCover() {
    elementCoverage = new ElementCoverage(1, 0, 0);
    String msg1 = "Return value for test end condition when required steps are covered";
    String msg2 = "Return value for suite end condition when required steps are covered";
    assertNoEnd(msg1, msg2);

    suite.addStep(t1);
    assertTestEnd(msg1);
    suite.endTest();
    assertSuiteEnd(msg2);
    suite.startTest(1);
    suite.addStep(t1);
    suite.addStep(t2);
    assertTestEnd(msg1);
    suite.endTest();
    assertSuiteEnd(msg2);
  }

  @Test
  public void twoStepToCover() {
    elementCoverage = new ElementCoverage(2, 0, 0);
    String msg1 = "Return value for test end condition when required steps are covered";
    String msg2 = "Return value for suite end condition when required steps are covered";
    assertNoEnd(msg1, msg2);

    suite.addStep(t1);
    assertNoTestEnd(msg1);
    suite.endTest();
    assertNoSuiteEnd(msg2);
    suite.startTest(1);
    suite.addStep(t1);
    suite.addStep(t2);
    assertTestEnd(msg1);
    suite.endTest();
    assertSuiteEnd(msg2);
  }

  @Test
  public void fiveStepsToCover() {
    elementCoverage = new ElementCoverage(5, 0, 0);
    String msg1 = "Return value for test end condition when required steps are covered";
    String msg2 = "Return value for suite end condition when required steps are covered";
    assertNoEnd(msg1, msg2);

    suite.addStep(t1);
    assertNoEnd(msg1, msg2);
    suite.endTest();
    suite.startTest(1);
    suite.addStep(t2);
    suite.addStep(t3);
    suite.addStep(t4);
    assertNoEnd(msg1, msg2);
    suite.addStep(t5);
    assertNoTestEnd(msg1);
    //test not yet ended
    assertNoSuiteEnd(msg2);
    suite.addStep(t1);
    assertTestEnd(msg1);
    suite.endTest();
    assertSuiteEnd(msg2);
  }

  @Test
  public void onePairToCover() {
    elementCoverage = new ElementCoverage(0, 1, 0);
    String msg1 = "Return value for test end condition when required pairs are covered";
    String msg2 = "Return value for suite end condition when required pairs are covered";
    assertNoEnd(msg1, msg2);

    suite.addStep(t1);
    assertTestEnd(msg1);
    suite.endTest();
    assertSuiteEnd(msg2);
    suite.startTest(1);
    suite.addStep(t1);
    suite.addStep(t2);
    assertTestEnd(msg1);
    suite.endTest();
    assertSuiteEnd(msg2);
  }

  @Test
  public void twoPairsToCover() {
    elementCoverage = new ElementCoverage(0, 2, 0);
    String msg1 = "Return value for test end condition when required pairs are covered";
    String msg2 = "Return value for suite end condition when required pairs are covered";
    assertNoEnd(msg1, msg2);

    suite.addStep(t1);
    assertNoTestEnd(msg1);
    suite.endTest();
    assertNoSuiteEnd(msg2);
    suite.startTest(1);
    suite.addStep(t1);
    suite.addStep(t2);
    assertTestEnd(msg1);
    suite.endTest();
    assertSuiteEnd(msg2);
  }

  @Test
  public void fivePairsToCover() {
    elementCoverage = new ElementCoverage(0, 5, 0);
    String msg1 = "Return value for test end condition when required pairs are covered";
    String msg2 = "Return value for suite end condition when required pairs are covered";
    assertNoEnd(msg1, msg2);

    suite.addStep(t1);
    assertNoEnd(msg1, msg2);
    suite.endTest();
    suite.startTest(1);
    suite.addStep(t2);
    suite.addStep(t3);
    suite.addStep(t4);
    assertNoEnd(msg1, msg2);
    suite.addStep(t5);
    assertNoTestEnd(msg1);
    //test not yet ended
    assertNoSuiteEnd(msg2);
    suite.addStep(t1);
    assertTestEnd(msg1);
    suite.endTest();
    assertSuiteEnd(msg2);
  }

  @Test
  public void oneRequirementToCover() {
    elementCoverage = new ElementCoverage(0, 0, 1);
    String msg1 = "Return value for test end condition when required requirements are covered";
    String msg2 = "Return value for suite end condition when required requirements are covered";
    assertNoEnd(msg1, msg2);

    suite.addStep(t1);
    suite.coveredRequirement("req1");
    assertTestEnd(msg1);
    suite.endTest();
    assertSuiteEnd(msg2);
    suite.startTest(1);
    suite.addStep(t1);
    suite.addStep(t2);
    //should not end as the test covers no requirements
    assertNoTestEnd(msg1);
    suite.endTest();
    //should end as the previous test covered the requirement
    assertSuiteEnd(msg2);
  }

  @Test
  public void tooManySteps() {
    OSMOTester osmo = new OSMOTester();
    osmo.setModelFactory(new ReflectiveModelFactory(VariableModel2.class));
    Length length1 = new Length(1);
    osmo.setTestEndCondition(new ElementCoverage(5, 0, 0));
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(444);
      fail("Generation with coverage for to many test steps (transitions) should fail.");
    } catch (IllegalArgumentException e) {
      //Expected
      assertEquals("Reported e", "Too many steps requested (model has 3, requested 5).", e.getMessage());
    }
  }

  @Test
  public void tooManyPairs() {
    OSMOTester osmo = new OSMOTester();
    osmo.setModelFactory(new ReflectiveModelFactory(VariableModel2.class));
    Length length1 = new Length(1);
    osmo.setTestEndCondition(new ElementCoverage(0, 11, 0));
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(444);
      fail("Generation with coverage for too many test step pairs should fail.");
    } catch (IllegalArgumentException e) {
      //Expected
      assertEquals("Reported e", "Too many pairs requested (model has 10, requested 11).", e.getMessage());
    }
  }

  @Test
  public void tooManyRequirements() {
    OSMOTester osmo = new OSMOTester();
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(new CalculatorModel());
    osmo.setModelFactory(factory);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(new ElementCoverage(0, 0, 3));
    osmo.setSuiteEndCondition(length1);
    try {
      osmo.generate(444);
      fail("Generation with coverage for too many test step pairs should fail.");
    } catch (IllegalArgumentException e) {
      //Expected
      assertEquals("Reported e", "Too many requirements requested (model has 2, requested 3).", e.getMessage());
    }
  }

  @Test
  public void allWithGeneration() {
    OSMOTester osmo = new OSMOTester();
    osmo.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    Length length1 = new Length(1);
    osmo.setTestEndCondition(new ElementCoverage(3, 3, 2));
    osmo.setSuiteEndCondition(length1);
    osmo.generate(444);
    String expected = "[TestCase:[start, increase, increase, increase, decrease]]";
    String actual = osmo.getSuite().getAllTestCases().toString();
    assertEquals("Generated tests", expected, actual);
  }

  private void assertNoSuiteEnd(String msg) {
    boolean end = elementCoverage.endSuite(suite, fsm);
    assertEquals(msg, false, end);
  }

  private void assertNoTestEnd(String msg) {
    boolean end = elementCoverage.endTest(suite, fsm);
    assertEquals(msg, false, end);
  }


  private void assertSuiteEnd(String msg) {
    boolean end = elementCoverage.endSuite(suite, fsm);
    assertEquals(msg, true, end);
  }

  private void assertTestEnd(String msg) {
    boolean end = elementCoverage.endTest(suite, fsm);
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
