package osmo.tester.endconditions;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.endcondition.TransitionCoverage;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import static junit.framework.Assert.*;

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
    Logger.debug = true;
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
  public void coverage70p() {
    suite.startTest();
    suite.addStep(t1);
    suite.addStep(t3);
    tc = new TransitionCoverage(0.7);
    assertEnd(false);
    suite.addStep(t4);
    suite.addStep(t5);
    suite.addStep(t6);
    suite.addStep(t7);
    assertEnd(false);
    suite.addStep(t8);
    assertEnd(true);
  }

  @Test
  public void coverage0p() {
    suite.startTest();
    tc = new TransitionCoverage(0.0);
    assertEnd(true);
  }

  @Test
  public void coverage100p() {
    tc = new TransitionCoverage(1.0);
    suite.startTest();
    assertEnd(false);
    suite.addStep(t1);
    assertEnd(false);
    suite.addStep(t2);
    assertEnd(false);
    suite.addStep(t3);
    assertEnd(false);
    suite.addStep(t4);
    assertEnd(false);
    suite.addStep(t5);
    assertEnd(false);
    suite.addStep(t6);
    assertEnd(false);
    suite.addStep(t7);
    assertEnd(false);
    suite.addStep(t8);
    assertEnd(false);
    suite.addStep(t9);
    assertEnd(false);
    suite.addStep(t10);
    assertEnd(true);
  }

  @Test
  public void coverage150p() {
    tc = new TransitionCoverage(1.5);
    suite.startTest();
    assertEnd(false);
    suite.addStep(t1);
    assertEnd(false);
    suite.addStep(t2);
    assertEnd(false);
    suite.addStep(t3);
    assertEnd(false);
    suite.addStep(t4);
    assertEnd(false);
    suite.addStep(t5);
    assertEnd(false);
    suite.addStep(t6);
    assertEnd(false);
    suite.addStep(t7);
    assertEnd(false);
    suite.addStep(t8);
    assertEnd(false);
    suite.addStep(t9);
    assertEnd(false);
    suite.addStep(t10);
    suite.addStep(t1);
    suite.addStep(t2);
    suite.addStep(t3);
    suite.addStep(t5);
    assertEnd(false);
    suite.addStep(t7);
    assertEnd(true);
  }

  @Test
  public void coverage150pOverlap() {
    tc = new TransitionCoverage(1.5);
    suite.startTest();
    assertEnd(false);
    suite.addStep(t1);
    assertEnd(false);
    suite.addStep(t2);
    assertEnd(false);
    suite.addStep(t3);
    assertEnd(false);
    suite.addStep(t4);
    assertEnd(false);
    suite.addStep(t5);
    assertEnd(false);
    suite.addStep(t6);
    assertEnd(false);
    suite.addStep(t7);
    assertEnd(false);
    suite.addStep(t8);
    assertEnd(false);
    suite.addStep(t9);
    assertEnd(false);
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
    assertEnd(false);
    suite.addStep(t7);
    assertEnd(true);
  }

  @Test
  public void coverage50pOverlap() {
    tc = new TransitionCoverage(0.5);
    suite.startTest();
    assertEnd(false);
    suite.addStep(t1);
    assertEnd(false);
    suite.addStep(t2);
    assertEnd(false);
    suite.addStep(t2);
    assertEnd(false);
    suite.addStep(t2);
    assertEnd(false);
    suite.addStep(t9);
    assertEnd(false);
    suite.addStep(t6);
    assertEnd(false);
    suite.addStep(t8);
    assertEnd(true);
    suite.addStep(t8);
    assertEnd(true);
    suite.addStep(t9);
    assertEnd(true);
  }

  private void assertEnd(boolean expected) {
    boolean actual = tc.checkThreshold(suite, fsm);
    assertEquals("Transition Coverage should end with "+suite.totalSteps()+"/10", expected, actual);
  }
}
