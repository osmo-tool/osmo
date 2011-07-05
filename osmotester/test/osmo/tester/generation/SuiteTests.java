package osmo.tester.generation;

import org.junit.Test;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import static junit.framework.Assert.assertEquals;

/**
 * @author Teemu Kanstren
 */
public class SuiteTests {
  @Test
  public void validSuite() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.add(new FSMTransition("t1"));
    suite.add(new FSMTransition("t2"));
    suite.endTest();
    suite.startTest();
    suite.add(new FSMTransition("t1"));
    suite.add(new FSMTransition("t2"));
    assertEquals("Number of test cases in test suite", 1, suite.getHistory().size());
    assertEquals("Number of steps in current test", 2, suite.currentSteps());
    assertEquals("Number of total steps in test suite", 4, suite.totalSteps());
    suite.endTest();
    assertEquals("Number of steps in current test", 0, suite.currentSteps());
    assertEquals("Number of test cases in test suite", 2, suite.getHistory().size());
  }

}
