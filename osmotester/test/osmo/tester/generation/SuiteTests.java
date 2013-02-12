package osmo.tester.generation;

import org.junit.Test;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.suiteoptimizer.coverage.ScoreConfiguration;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class SuiteTests {
  @Test
  public void validSuite() {
    TestSuite suite = new TestSuite();
    suite.init(new ScoreConfiguration());
    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    assertEquals("Number of test cases in test suite", 1, suite.getFinishedTestCases().size());
    assertEquals("Number of steps in current test", 2, suite.currentSteps());
    assertEquals("Number of total steps in test suite", 4, suite.totalSteps());
    suite.endTest();
    assertEquals("Number of steps in current test", 0, suite.currentSteps());
    assertEquals("Number of test cases in test suite", 2, suite.getFinishedTestCases().size());
  }

  @Test
  public void totalCoverage() {
    TestSuite suite = new TestSuite();
    suite.init(new ScoreConfiguration());
    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    suite.covered("r1");

    int a1 = suite.getCurrentTest().getUniqueRequirementCoverage().size();
    assertEquals("Coverage for requirements", 1, a1);
    int a2 = suite.getCurrentTest().getCoveredTransitions().size();
    assertEquals("Coverage for transitions", 2, a2);
    suite.endTest();

    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    suite.covered("r1");

    a1 = suite.getCurrentTest().getUniqueRequirementCoverage().size();
    assertEquals("Added coverage for requirements", 1, a1);
    a2 = suite.getCurrentTest().getCoveredTransitions().size();
    assertEquals("Added coverage for transitions", 2, a2);
    suite.endTest();

    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t3"));
    suite.covered("r2");

    a1 = suite.getCurrentTest().getUniqueRequirementCoverage().size();
    assertEquals("Coverage for requirements", 1, a1);
    a2 = suite.getCurrentTest().getCoveredTransitions().size();
    assertEquals("Coverage for transitions", 2, a2);
    suite.endTest();

    suite.startTest();
    suite.addStep(new FSMTransition("t3"));
    suite.addStep(new FSMTransition("t4"));
    suite.covered("r3");
    suite.addStep(new FSMTransition("t5"));
    suite.covered("r4");

    a1 = suite.getCurrentTest().getUniqueRequirementCoverage().size();
    assertEquals("Coverage for requirements", 2, a1);
    a2 = suite.getCurrentTest().getCoveredTransitions().size();
    assertEquals("Coverage for transitions", 3, a2);
    suite.endTest();

    assertEquals("Number of steps in current test", 0, suite.currentSteps());
    assertEquals("Number of test cases in test suite", 4, suite.getFinishedTestCases().size());
  }

}
