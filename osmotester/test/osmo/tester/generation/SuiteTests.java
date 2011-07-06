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
    assertEquals("Number of test cases in test suite", 1, suite.getTestCases().size());
    assertEquals("Number of steps in current test", 2, suite.currentSteps());
    assertEquals("Number of total steps in test suite", 4, suite.totalSteps());
    suite.endTest();
    assertEquals("Number of steps in current test", 0, suite.currentSteps());
    assertEquals("Number of test cases in test suite", 2, suite.getTestCases().size());
  }

  @Test
  public void addedCoverage() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.add(new FSMTransition("t1"));
    suite.add(new FSMTransition("t2"));
    suite.covered("r1");

    int a1 = suite.getCurrent().getAddedRequirementsCoverage().size();
    assertEquals("Added coverage for requirements", 1, a1);
    int a2 = suite.getCurrent().getAddedTransitionCoverage().size();
    assertEquals("Added coverage for transitions", 2, a2);
    suite.endTest();

    suite.startTest();
    suite.add(new FSMTransition("t1"));
    suite.add(new FSMTransition("t2"));
    suite.covered("r1");

    a1 = suite.getCurrent().getAddedRequirementsCoverage().size();
    assertEquals("Added coverage for requirements", 0, a1);
    a2 = suite.getCurrent().getAddedTransitionCoverage().size();
    assertEquals("Added coverage for transitions", 0, a2);
    suite.endTest();

    suite.startTest();
    suite.add(new FSMTransition("t1"));
    suite.add(new FSMTransition("t3"));
    suite.covered("r2");

    a1 = suite.getCurrent().getAddedRequirementsCoverage().size();
    assertEquals("Added coverage for requirements", 1, a1);
    a2 = suite.getCurrent().getAddedTransitionCoverage().size();
    assertEquals("Added coverage for transitions", 1, a2);
    suite.endTest();

    suite.startTest();
    suite.add(new FSMTransition("t3"));
    suite.add(new FSMTransition("t4"));
    suite.covered("r3");
    suite.add(new FSMTransition("t5"));
    suite.covered("r4");

    a1 = suite.getCurrent().getAddedRequirementsCoverage().size();
    assertEquals("Added coverage for requirements", 2, a1);
    a2 = suite.getCurrent().getAddedTransitionCoverage().size();
    assertEquals("Added coverage for transitions", 2, a2);
    suite.endTest();

    assertEquals("Number of steps in current test", 0, suite.currentSteps());
    assertEquals("Number of test cases in test suite", 4, suite.getTestCases().size());
  }

}
