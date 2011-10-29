package osmo.tester.endconditions;

import org.junit.Before;
import org.junit.Test;
import osmo.common.log.Logger;
import osmo.tester.generator.endcondition.TransitionCoverage;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import static junit.framework.Assert.assertEquals;

/**
 * @author Teemu Kanstren
 */
public class TransitionCoverageTests {
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
  private TransitionCoverage tc;

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
  }

  @Test
  public void suiteCoverage70p() {
    suite.startTest();
    suite.addStep(t1);
    suite.addStep(t3);
    tc = new TransitionCoverage(0.7);
    assertEndSuite(false);
    suite.addStep(t4);
    suite.addStep(t5);
    suite.addStep(t6);
    suite.addStep(t7);
    assertEndSuite(false);
    suite.addStep(t8);
    assertEndSuite(true);
  }

  @Test
  public void coverage0p() {
    suite.startTest();
    tc = new TransitionCoverage(0.0);
    assertEndSuite(true);
    assertEndTest(true);
  }

  @Test
  public void suiteCoverage100p() {
    tc = new TransitionCoverage(1.0);
    suite.startTest();
    assertEndSuite(false);
    suite.addStep(t1);
    assertEndSuite(false);
    suite.addStep(t2);
    assertEndSuite(false);
    suite.addStep(t3);
    assertEndSuite(false);
    suite.addStep(t4);
    assertEndSuite(false);
    suite.addStep(t5);
    assertEndSuite(false);
    suite.addStep(t6);
    assertEndSuite(false);
    suite.addStep(t7);
    assertEndSuite(false);
    suite.addStep(t8);
    assertEndSuite(false);
    suite.addStep(t9);
    assertEndSuite(false);
    suite.addStep(t10);
    assertEndSuite(true);
  }

  @Test
  public void suiteCoverage150p() {
    tc = new TransitionCoverage(1.5);
    suite.startTest();
    assertEndSuite(false);
    suite.addStep(t1);
    assertEndSuite(false);
    suite.addStep(t2);
    assertEndSuite(false);
    suite.addStep(t3);
    assertEndSuite(false);
    suite.addStep(t4);
    assertEndSuite(false);
    suite.addStep(t5);
    assertEndSuite(false);
    suite.addStep(t6);
    assertEndSuite(false);
    suite.addStep(t7);
    assertEndSuite(false);
    suite.addStep(t8);
    assertEndSuite(false);
    suite.addStep(t9);
    assertEndSuite(false);
    suite.addStep(t10);
    suite.addStep(t1);
    suite.addStep(t2);
    suite.addStep(t3);
    suite.addStep(t5);
    assertEndSuite(false);
    suite.addStep(t7);
    assertEndSuite(true);
  }

  @Test
  public void suiteCoverage150pOverlap() {
    tc = new TransitionCoverage(1.5);
    suite.startTest();
    assertEndSuite(false);
    suite.addStep(t1);
    assertEndSuite(false);
    suite.addStep(t2);
    assertEndSuite(false);
    suite.addStep(t3);
    assertEndSuite(false);
    suite.addStep(t4);
    assertEndSuite(false);
    suite.addStep(t5);
    assertEndSuite(false);
    suite.addStep(t6);
    assertEndSuite(false);
    suite.addStep(t7);
    assertEndSuite(false);
    suite.addStep(t8);
    assertEndSuite(false);
    suite.addStep(t9);
    assertEndSuite(false);
    suite.addStep(t10);
    suite.addStep(t1);
    suite.addStep(t2);
    suite.addStep(t2);
    suite.addStep(t2);
    suite.addStep(t2);
    suite.addStep(t3);
    suite.addStep(t3);
    suite.addStep(t3);
    suite.addStep(t3);
    suite.addStep(t3);
    suite.addStep(t5);
    assertEndSuite(false);
    suite.addStep(t7);
    assertEndSuite(true);
  }

  @Test
  public void suiteCoverage50pOverlap() {
    tc = new TransitionCoverage(0.5);
    suite.startTest();
    assertEndSuite(false);
    suite.addStep(t1);
    assertEndSuite(false);
    suite.addStep(t2);
    assertEndSuite(false);
    suite.addStep(t2);
    assertEndSuite(false);
    suite.addStep(t2);
    assertEndSuite(false);
    suite.addStep(t9);
    assertEndSuite(false);
    suite.addStep(t6);
    assertEndSuite(false);
    suite.addStep(t8);
    assertEndSuite(true);
    suite.addStep(t8);
    assertEndSuite(true);
    suite.addStep(t9);
    assertEndSuite(true);
  }

  @Test
  public void testCoverage50pTwoTests() {
    tc = new TransitionCoverage(0.5);
    suite.startTest();
    suite.addStep(t1);
    suite.addStep(t2);
    suite.addStep(t3);
    suite.addStep(t4);
    suite.addStep(t5);
    suite.addStep(t6);
    suite.endTest();
    suite.startTest();
    suite.addStep(t2);
    suite.addStep(t3);
    suite.addStep(t8);
    assertEndSuite(true);
    assertEndTest(false);
    suite.addStep(t5);
    suite.addStep(t6);
    assertEndSuite(true);
    assertEndTest(true);
  }


  private void assertEndSuite(boolean expected) {
    boolean actual = tc.checkThreshold(suite, fsm, true);
    assertEquals("Transition Coverage should end with "+suite.totalSteps()+"/10", expected, actual);
  }

  private void assertEndTest(boolean expected) {
    boolean actual = tc.checkThreshold(suite, fsm, false);
    assertEquals("Transition Coverage should end with " + suite.getCurrentTest().getSteps().size() + "/10", expected, actual);
  }
}
