package osmo.tester.optimizer;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.testmodels.CalculatorModel;
import osmo.tester.testmodels.RandomValueModel;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class GreedyTests {
  private ScoreConfiguration gc;

  @Before
  public void initTest() {
    OSMOConfiguration.setSeed(234);
    gc = new ScoreConfiguration();
    gc.setStepWeight(0);
    gc.setLengthWeight(0);
    gc.setPairsWeight(0);
    gc.setDefaultValueWeight(0);
    gc.setVariableCountWeight(0);
    gc.setRequirementWeight(0);
  }

  private int scoreFor(Collection<TestCase> tests) {
    TestCoverage tc = new TestCoverage(tests);
    ScoreCalculator scoreCalculator = new ScoreCalculator(gc);
    return scoreCalculator.calculateFitness(tc);
  }

  @Test
  public void requirementOptimizer3TestsNoOverlap() {
    TestSuite suite = new TestSuite();
    suite.init();
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

    gc.setRequirementWeight(1);
    GreedyOptimizer optimizer = new GreedyOptimizer(gc, new LengthProbability(1, 10, 0.1d));
    List<TestCase> tests = optimizer.sortAndPrune(suite.getFinishedTestCases());
    assertEquals("Number of tests should be reduced after pruning useless ones.", 2, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    Collection<String> tags1 = testCase1.getUniqueRequirementCoverage();
    Collection<String> tags2 = testCase2.getUniqueRequirementCoverage();
    assertEquals("Number of new tags covered by test 1.", 3, tags1.size());
    assertEquals("Number of new tags covered by test 2.", 1, tags2.size());
    assertEquals("Coverage score", 4, scoreFor(tests));
  }

  @Test
  public void requirementOptimizer3TestsWithOverlap() {
    gc.setRequirementWeight(1);
    GreedyOptimizer optimizer = new GreedyOptimizer(gc, new LengthProbability(1, 10, 0.2d));
    TestSuite suite = createSuite1();
    List<TestCase> tests = optimizer.sortAndPrune(suite.getFinishedTestCases());
    assertEquals("Number of tests should be reduced after pruning useless ones.", 1, tests.size());
    TestCase testCase1 = tests.get(0);
    Collection<String> tags1 = testCase1.getUniqueRequirementCoverage();
    //the first test covers all tags so the rest are discarded
    assertEquals("Number of new tags covered by test 1.", 2, tags1.size());
    assertEquals("Coverage score", 2, scoreFor(tests));
  }

  @Test
  public void requirementOptimizer3TestsWithCunningOverlap() {
    GreedyOptimizer optimizer = new GreedyOptimizer(gc, new LengthProbability(1, 10, 0.1d));
    gc.setRequirementWeight(1);
    TestSuite suite = createSuite2();
    List<TestCase> tests = optimizer.sortAndPrune(suite.getFinishedTestCases());
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> tags1 = testCase1.getUniqueRequirementCoverage();
    Collection<String> tags2 = testCase2.getUniqueRequirementCoverage();
    Collection<String> tags3 = testCase3.getUniqueRequirementCoverage();
    assertEquals("Number of new tags covered by test 1.", 3, tags1.size());
    assertEquals("Number of new tags covered by test 2.", 2, tags2.size());
    assertEquals("Number of new tags covered by test 3.", 1, tags3.size());
    assertEquals("Coverage score", 6, scoreFor(tests));
  }

  private TestSuite createSuite1() {
    TestSuite suite = new TestSuite();
    suite.init();
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
    suite.init();
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
    GreedyOptimizer optimizer = new GreedyOptimizer(gc, new LengthProbability(1, 10, 0.1d));
    gc.setStepWeight(1);
    List<TestCase> tests = optimizer.sortAndPrune(suite.getFinishedTestCases());
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> transitions1 = testCase1.getCoveredTransitions();
    Collection<String> transitions2 = testCase2.getCoveredTransitions();
    Collection<String> transitions3 = testCase3.getCoveredTransitions();
    assertEquals("Number of new transitions covered by test 1.", 4, transitions1.size());
    assertEquals("Number of new transitions covered by test 2.", 3, transitions2.size());
    assertEquals("Number of new transitions covered by test 3.", 1, transitions3.size());
    assertEquals("Coverage score", 8, scoreFor(tests));
  }

  @Test
  public void transitionOptimizer3TestsWithOverlap() {
    TestSuite suite = createSuite2();
    GreedyOptimizer optimizer = new GreedyOptimizer(gc, new LengthProbability(1, 10, 0.1d));
    gc.setStepWeight(1);
    List<TestCase> tests = optimizer.sortAndPrune(suite.getFinishedTestCases());
    assertEquals("Number of tests should be reduced after pruning useless ones.", 1, tests.size());
    TestCase testCase1 = tests.get(0);
    Collection<String> transitions1 = testCase1.getCoveredTransitions();
    //the first one should cover all of the ones by the rest as well
    assertEquals("Number of new transitions covered by test 1.", 4, transitions1.size());
  }

  @Test
  public void ratOptimizer() {
    TestSuite suite = createSuite2();
    GreedyOptimizer optimizer = new GreedyOptimizer(gc, new LengthProbability(1, 10, 0.1d));
    gc.setStepWeight(1);
    gc.setRequirementWeight(4);
    List<TestCase> tests = optimizer.sortAndPrune(suite.getFinishedTestCases());
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> transitions1 = testCase1.getCoveredTransitions();
    Collection<String> transitions2 = testCase2.getCoveredTransitions();
    Collection<String> transitions3 = testCase3.getCoveredTransitions();
    assertEquals("Number of new transitions covered by test 1.", 4, transitions1.size());
    assertEquals("Number of new transitions covered by test 2.", 1, transitions2.size());
    assertEquals("Number of new transitions covered by test 3.", 3, transitions3.size());
    Collection<String> reqs1 = testCase1.getUniqueRequirementCoverage();
    Collection<String> reqs2 = testCase2.getUniqueRequirementCoverage();
    Collection<String> reqs3 = testCase3.getUniqueRequirementCoverage();
    assertEquals("Number of new tags covered by test 1.", 3, reqs1.size());
    assertEquals("Number of new tags covered by test 2.", 2, reqs2.size());
    assertEquals("Number of new tags covered by test 3.", 1, reqs3.size());
    assertEquals("Coverage score", 28, scoreFor(tests));
  }

  @Test
  public void generation() {
    OSMOConfiguration.setSeed(8);
    ScoreConfiguration config = new ScoreConfiguration();
    GreedyOptimizer optimizer = new GreedyOptimizer(config, new LengthProbability(1, 5, 0.1d));
    optimizer.addModelClass(CalculatorModel.class);
    List<TestCase> tests = optimizer.search();
    assertEquals("Number of tests from greedy", 3, tests.size());
    assertEquals("First test from greedy", "[start, increase, decrease, increase, increase]", tests.get(0).getAllTransitionNames().toString());
    assertEquals("Second test from greedy", "[start, increase, increase, decrease, decrease]", tests.get(1).getAllTransitionNames().toString());
    assertEquals("Third test from greedy", "[start, increase, increase, increase, increase]", tests.get(2).getAllTransitionNames().toString());
  }
  
  @Test
  public void timeOut() {
    OSMOConfiguration.setSeed(8);
    ScoreConfiguration gc = new ScoreConfiguration();
    gc.setStepWeight(0);
    gc.setPairsWeight(0);
    gc.setDefaultValueWeight(1);
    gc.setVariableCountWeight(0);
    gc.setRequirementWeight(0);
    GreedyOptimizer greedy = new GreedyOptimizer(gc, 100, new LengthProbability(10, 1d));
    greedy.setTimeout(1);
    greedy.addModelClass(RandomValueModel.class);
    long start = System.currentTimeMillis();
    greedy.search();
    long end = System.currentTimeMillis();
    long diff = end - start;
    assertTrue("Timeout should be 1-3s was "+diff+" ms", diff > 1000 && diff < 3000);
  }
}
