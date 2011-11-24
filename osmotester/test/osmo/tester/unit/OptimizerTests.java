package osmo.tester.unit;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.SearchConfiguration;
import osmo.tester.optimizer.offline.GreedyOptimizer;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/** @author Teemu Kanstren */
public class OptimizerTests {
  private SearchConfiguration sc;

  @Before
  public void initTest() {
    sc = new SearchConfiguration(null);
    sc.setTransitionWeight(0);
    sc.setLengthWeight(0);
    sc.setPairsWeight(0);
    sc.setValueWeight(0);
    sc.setVariableWeight(0);
    sc.setRequirementWeight(0);
  }

  @Test
  public void requirementsOptimizer3TestsNoOverlap() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.covered("r1");
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("t2"));
    suite.addStep(new FSMTransition("t3"));
    suite.covered("r2");
    suite.addStep(new FSMTransition("t4"));
    suite.covered("r3");
    suite.covered("r4");
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("t5"));
    suite.addStep(new FSMTransition("t6"));
    suite.addStep(new FSMTransition("t7"));
    suite.endTest();

    sc.setRequirementWeight(1);
    GreedyOptimizer optimizer = new GreedyOptimizer(sc);
    List<TestCase> tests = optimizer.createSortedTestSet(3, suite.getFinishedTestCases());
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> reqs1 = testCase1.getUniqueRequirementsCoverage();
    Collection<String> reqs2 = testCase2.getUniqueRequirementsCoverage();
    Collection<String> reqs3 = testCase3.getUniqueRequirementsCoverage();
    assertEquals("Number of new requirements covered by test 1.", 3, reqs1.size());
    assertEquals("Number of new requirements covered by test 2.", 1, reqs2.size());
    assertEquals("Number of new requirements covered by test 3.", 0, reqs3.size());
  }

  @Test
  public void requirementsOptimizer3TestsWithOverlap() {
    sc.setRequirementWeight(1);
    GreedyOptimizer optimizer = new GreedyOptimizer(sc);
    TestSuite suite = createSuite1();
    List<TestCase> tests = optimizer.createSortedTestSet(3, suite.getFinishedTestCases());
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> reqs1 = testCase1.getUniqueRequirementsCoverage();
    Collection<String> reqs2 = testCase2.getUniqueRequirementsCoverage();
    Collection<String> reqs3 = testCase3.getUniqueRequirementsCoverage();
    //the first test covers all requirements so the rest are basically random (as iterated)
    assertEquals("Number of new requirements covered by test 1.", 2, reqs1.size());
    assertEquals("Number of new requirements covered by test 2.", 1, reqs2.size());
    assertEquals("Number of new requirements covered by test 3.", 0, reqs3.size());
  }

  @Test
  public void requirementsOptimizer3TestsWithCunningOverlap() {
    GreedyOptimizer optimizer = new GreedyOptimizer(sc);
    sc.setRequirementWeight(1);
    TestSuite suite = createSuite2();
    List<TestCase> tests = optimizer.createSortedTestSet(3, suite.getFinishedTestCases());
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> reqs1 = testCase1.getUniqueRequirementsCoverage();
    Collection<String> reqs2 = testCase2.getUniqueRequirementsCoverage();
    Collection<String> reqs3 = testCase3.getUniqueRequirementsCoverage();
    assertEquals("Number of new requirements covered by test 1.", 3, reqs1.size());
    assertEquals("Number of new requirements covered by test 2.", 2, reqs2.size());
    assertEquals("Number of new requirements covered by test 3.", 1, reqs3.size());
  }

  private TestSuite createSuite1() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.covered("r1");
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("t2"));
    suite.addStep(new FSMTransition("t3"));
    suite.covered("r2");
    suite.addStep(new FSMTransition("t4"));
    suite.covered("r1");
    suite.covered("r2");
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("t5"));
    suite.addStep(new FSMTransition("t6"));
    suite.addStep(new FSMTransition("t7"));
    suite.addStep(new FSMTransition("t8"));
    suite.endTest();
    return suite;
  }

  private TestSuite createSuite2() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.covered("r5");
    suite.covered("r1");
    suite.endTest();

    suite.startTest();
    suite.addStep(new FSMTransition("t2"));
    suite.addStep(new FSMTransition("t3"));
    suite.covered("r2");
    suite.addStep(new FSMTransition("t4"));
    suite.endTest();

    suite.startTest();
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    suite.covered("r3");
    suite.addStep(new FSMTransition("t3"));
    suite.covered("r4");
    suite.covered("r6");
    suite.addStep(new FSMTransition("t4"));
    suite.endTest();
    return suite;
  }

  @Test
  public void transitionOptimizer3TestsNoOverlap() {
    TestSuite suite = createSuite1();
    GreedyOptimizer optimizer = new GreedyOptimizer(sc);
    sc.setTransitionWeight(1);
    List<TestCase> tests = optimizer.createSortedTestSet(3, suite.getFinishedTestCases());
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<FSMTransition> transitions1 = testCase1.getCoveredTransitions();
    Collection<FSMTransition> transitions2 = testCase2.getCoveredTransitions();
    Collection<FSMTransition> transitions3 = testCase3.getCoveredTransitions();
    assertEquals("Number of new transitions covered by test 1.", 4, transitions1.size());
    assertEquals("Number of new transitions covered by test 2.", 3, transitions2.size());
    assertEquals("Number of new transitions covered by test 3.", 1, transitions3.size());
  }

  @Test
  public void transitionOptimizer3TestsWithOverlap() {
    TestSuite suite = createSuite2();
    GreedyOptimizer optimizer = new GreedyOptimizer(sc);
    sc.setTransitionWeight(1);
    List<TestCase> tests = optimizer.createSortedTestSet(3, suite.getFinishedTestCases());
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<FSMTransition> transitions1 = testCase1.getCoveredTransitions();
    Collection<FSMTransition> transitions2 = testCase2.getCoveredTransitions();
    Collection<FSMTransition> transitions3 = testCase3.getCoveredTransitions();
    assertEquals("Number of new transitions covered by test 1.", 4, transitions1.size());
    assertEquals("Number of new transitions covered by test 2.", 3, transitions2.size());
    assertEquals("Number of new transitions covered by test 3.", 1, transitions3.size());
  }

  @Test
  public void ratOptimizer() {
    TestSuite suite = createSuite2();
    GreedyOptimizer optimizer = new GreedyOptimizer(sc);
    sc.setTransitionWeight(1);
    sc.setRequirementWeight(4);
    List<TestCase> tests = optimizer.createSortedTestSet(3, suite.getFinishedTestCases());
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<FSMTransition> transitions1 = testCase1.getCoveredTransitions();
    Collection<FSMTransition> transitions2 = testCase2.getCoveredTransitions();
    Collection<FSMTransition> transitions3 = testCase3.getCoveredTransitions();
    assertEquals("Number of new transitions covered by test 1.", 4, transitions1.size());
    assertEquals("Number of new transitions covered by test 2.", 1, transitions2.size());
    assertEquals("Number of new transitions covered by test 3.", 3, transitions3.size());
    Collection<String> reqs1 = testCase1.getUniqueRequirementsCoverage();
    Collection<String> reqs2 = testCase2.getUniqueRequirementsCoverage();
    Collection<String> reqs3 = testCase3.getUniqueRequirementsCoverage();
    assertEquals("Number of new requirements covered by test 1.", 3, reqs1.size());
    assertEquals("Number of new requirements covered by test 2.", 2, reqs2.size());
    assertEquals("Number of new requirements covered by test 3.", 1, reqs3.size());
  }
}
