package osmo.tester.unit;

import org.junit.Test;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class HistoryTests {
  @Test
  public void historyContainsByName() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("bob"));
    suite.addStep(new FSMTransition("alice"));
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("bob2"));
    suite.addStep(new FSMTransition("alice2"));
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("bob3"));
    suite.addStep(new FSMTransition("alice3"));
    suite.endTest();
    assertTrue(suite.contains(new FSMTransition("bob")));
    assertTrue(suite.contains(new FSMTransition("bob2")));
    assertTrue(suite.contains(new FSMTransition("bob3")));
    assertFalse(suite.contains(new FSMTransition("bob4")));
  }

  @Test
  public void currentContainsByName() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("bob"));
    suite.addStep(new FSMTransition("alice"));
    assertTrue(suite.contains(new FSMTransition("bob")));
    assertFalse(suite.contains(new FSMTransition("bob4")));
  }

}
