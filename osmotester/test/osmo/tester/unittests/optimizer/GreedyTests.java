package osmo.tester.unittests.optimizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.GenerationResults;
import osmo.tester.optimizer.greedy.GreedyOptimizer;
import osmo.tester.optimizer.greedy.IterationListener;
import osmo.tester.optimizer.multiosmo.MultiOSMO;
import osmo.tester.unittests.testmodels.CalculatorModel;
import osmo.tester.unittests.testmodels.RandomValueModel;
import osmo.tester.unittests.testmodels.RandomValueModel4;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/** @author Teemu Kanstren */
public class GreedyTests {
  private ScoreConfiguration gc;
  private OSMOConfiguration oc;

  @Before
  public void initTest() {
    TestUtils.startOutputCapture();
    gc = new ScoreConfiguration();
    gc.setStepWeight(0);
    gc.setLengthWeight(0);
    gc.setStepPairWeight(0);
    gc.setDefaultValueWeight(0);
    gc.setVariableCountWeight(0);
    gc.setRequirementWeight(0);
    oc = new OSMOConfiguration();
  }

  @After
  public void resetAfter() {
    TestUtils.endOutputCapture();
  }

  private int scoreFor(Collection<TestCase> tests) {
    TestCoverage tc = new TestCoverage(tests);
    ScoreCalculator scoreCalculator = new ScoreCalculator(gc);
    return scoreCalculator.calculateScore(tc);
  }

  @Test
  public void requirementOptimizer3TestsNoOverlap() {
    TestSuite suite = new TestSuite();
    suite.startTest(1);
    suite.addStep(new FSMTransition("t1"));
    suite.coveredRequirement("r1");
    suite.endTest();
    suite.startTest(1);
    suite.addStep(new FSMTransition("t2"));
    suite.addStep(new FSMTransition("t3"));
    suite.coveredRequirement("r2");
    suite.addStep(new FSMTransition("t4"));
    suite.coveredRequirement("r3");
    suite.coveredRequirement("r4");
    suite.endTest();
    suite.startTest(1);
    suite.addStep(new FSMTransition("t5"));
    suite.addStep(new FSMTransition("t6"));
    suite.addStep(new FSMTransition("t7"));
    suite.endTest();

    gc.setRequirementWeight(1);
    List<TestCase> tests = GreedyOptimizer.sortAndPrune(1, suite.getAllTestCases(), new ScoreCalculator(gc), 0);
    assertEquals("Number of tests should be reduced after pruning useless ones.", 2, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    Collection<String> reqs1 = testCase1.getCoverage().getRequirements();
    Collection<String> reqs2 = testCase2.getCoverage().getRequirements();
    assertEquals("Number of new requirements covered by test 1.", 3, reqs1.size());
    assertEquals("Number of new requirementss covered by test 2.", 1, reqs2.size());
    assertEquals("Coverage score", 4, scoreFor(tests));
  }

  @Test
  public void requirementOptimizer3TestsWithOverlap() {
    gc.setRequirementWeight(1);
    TestSuite suite = createSuite1();
    List<TestCase> tests = GreedyOptimizer.sortAndPrune(1, suite.getAllTestCases(), new ScoreCalculator(gc), 0);
    assertEquals("Number of tests should be reduced after pruning useless ones.", 1, tests.size());
    TestCase testCase1 = tests.get(0);
    Collection<String> reqs1 = testCase1.getCoverage().getRequirements();
    //the first test covers all tags so the rest are discarded
    assertEquals("Number of new requirements covered by test 1.", 2, reqs1.size());
    assertEquals("Coverage score", 2, scoreFor(tests));
  }

  @Test
  public void requirementOptimizer3TestsWithCunningOverlap() {
    //GreedyOptimizer optimizer = new GreedyOptimizer(oc, gc);
    gc.setRequirementWeight(1);
    TestSuite suite = createSuite2();
    List<TestCase> tests = GreedyOptimizer.sortAndPrune(1, suite.getAllTestCases(), new ScoreCalculator(gc), 0);
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> reqs1 = testCase1.getCoverage().getRequirements();
    Collection<String> reqs2 = testCase2.getCoverage().getRequirements();
    Collection<String> reqs3 = testCase3.getCoverage().getRequirements();
    assertEquals("Number of new requirements covered by test 1.", 3, reqs1.size());
    assertEquals("Number of new requirements covered by test 2.", 2, reqs2.size());
    assertEquals("Number of new requirements covered by test 3.", 1, reqs3.size());
    assertEquals("Coverage score", 6, scoreFor(tests));
  }

  @Test
  public void optimizerOverlappingState() {
    //GreedyOptimizer optimizer = new GreedyOptimizer(oc, gc);
    gc.setStateWeight(1);
    gc.setStatePairWeight(100);
    TestSuite suite = createSuite3();
    List<TestCase> tests = GreedyOptimizer.sortAndPrune(1, suite.getAllTestCases(), new ScoreCalculator(gc), 0);
    assertEquals("Number of tests after sort and prune", 3, tests.size());
    assertEquals("Coverage score", 705, scoreFor(tests));
  }

  private TestSuite createSuite1() {
    TestSuite suite = new TestSuite();
    suite.startTest(1);
    suite.addStep(new FSMTransition("t1"));
    suite.coveredRequirement("r1");
    suite.endTest();
    suite.startTest(1);
    suite.addStep(new FSMTransition("t2"));
    suite.addStep(new FSMTransition("t3"));
    suite.coveredRequirement("r2");
    suite.addStep(new FSMTransition("t4"));
    suite.coveredRequirement("r1");
    suite.coveredRequirement("r2");
    suite.endTest();
    suite.startTest(1);
    suite.addStep(new FSMTransition("t5"));
    suite.addStep(new FSMTransition("t6"));
    suite.addStep(new FSMTransition("t7"));
    suite.addStep(new FSMTransition("t8"));
    suite.endTest();
    return suite;
  }

  private TestSuite createSuite2() {
    TestSuite suite = new TestSuite();
    suite.startTest(1);
    suite.addStep(new FSMTransition("t1"));
    suite.coveredRequirement("r5");
    suite.coveredRequirement("r1");
    suite.endTest();

    suite.startTest(1);
    suite.addStep(new FSMTransition("t2"));
    suite.addStep(new FSMTransition("t3"));
    suite.coveredRequirement("r2");
    suite.addStep(new FSMTransition("t4"));
    suite.endTest();

    suite.startTest(1);
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    suite.coveredRequirement("r3");
    suite.addStep(new FSMTransition("t3"));
    suite.coveredRequirement("r4");
    suite.coveredRequirement("r6");
    suite.addStep(new FSMTransition("t4"));
    suite.endTest();
    return suite;
  }

  private TestSuite createSuite3() {
    TestSuite suite = new TestSuite();
    suite.startTest(1);
    suite.addStep(new FSMTransition("t1"));
    suite.addUserCoverage("uc1", "1");
    suite.addUserCoverage("uc1", "2");
    suite.coveredRequirement("r5");
    suite.coveredRequirement("r1");
    suite.endTest();

    suite.startTest(1);
    suite.addStep(new FSMTransition("t2"));
    suite.addStep(new FSMTransition("t3"));
    suite.addUserCoverage("uc2", "1");
    suite.addUserCoverage("uc2", "2");
    suite.coveredRequirement("r2");
    suite.addStep(new FSMTransition("t4"));
    suite.addUserCoverage("uc2", "3");
    suite.addUserCoverage("uc2", "2");
    suite.endTest();

    suite.startTest(1);
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    suite.addUserCoverage("uc2", "3");
    suite.addUserCoverage("uc2", "2");
    suite.coveredRequirement("r3");
    suite.addStep(new FSMTransition("t3"));
    suite.coveredRequirement("r4");
    suite.coveredRequirement("r6");
    suite.addStep(new FSMTransition("t4"));
    suite.endTest();
    return suite;
  }

  @Test
  public void stepOptimizer3TestsNoOverlap() {
    TestSuite suite = createSuite1();
    gc.setStepWeight(1);
    List<TestCase> tests = GreedyOptimizer.sortAndPrune(1, suite.getAllTestCases(), new ScoreCalculator(gc), 0);
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> transitions1 = testCase1.getCoveredSteps();
    Collection<String> transitions2 = testCase2.getCoveredSteps();
    Collection<String> transitions3 = testCase3.getCoveredSteps();
    assertEquals("Number of new transitions covered by test 1.", 4, transitions1.size());
    assertEquals("Number of new transitions covered by test 2.", 3, transitions2.size());
    assertEquals("Number of new transitions covered by test 3.", 1, transitions3.size());
    assertEquals("Coverage score", 8, scoreFor(tests));
  }

  @Test
  public void stepOptimizer3TestsWithOverlap() {
    TestSuite suite = createSuite2();
    gc.setStepWeight(1);
    List<TestCase> tests = GreedyOptimizer.sortAndPrune(1, suite.getAllTestCases(), new ScoreCalculator(gc), 0);
    assertEquals("Number of tests should be reduced after pruning useless ones.", 1, tests.size());
    TestCase testCase1 = tests.get(0);
    Collection<String> transitions1 = testCase1.getCoveredSteps();
    //the first one should cover all of the ones by the rest as well
    assertEquals("Number of new transitions covered by test 1.", 4, transitions1.size());
  }

  @Test
  public void requirementsAndStepsOptimizer() {
    TestSuite suite = createSuite2();
    gc.setStepWeight(1);
    gc.setRequirementWeight(4);
    List<TestCase> tests = GreedyOptimizer.sortAndPrune(1, suite.getAllTestCases(), new ScoreCalculator(gc), 0);
    assertEquals("Number of tests after optimization should match that of before.", 3, tests.size());
    TestCase testCase1 = tests.get(0);
    TestCase testCase2 = tests.get(1);
    TestCase testCase3 = tests.get(2);
    Collection<String> steps1 = testCase1.getCoveredSteps();
    Collection<String> steps2 = testCase2.getCoveredSteps();
    Collection<String> steps3 = testCase3.getCoveredSteps();
    assertEquals("Number of new steps covered by test 1.", 4, steps1.size());
    assertEquals("Number of new steps covered by test 2.", 1, steps2.size());
    assertEquals("Number of new steps covered by test 3.", 3, steps3.size());
    Collection<String> reqs1 = testCase1.getCoverage().getRequirements();
    Collection<String> reqs2 = testCase2.getCoverage().getRequirements();
    Collection<String> reqs3 = testCase3.getCoverage().getRequirements();
    assertEquals("Number of new requirements covered by test 1.", 3, reqs1.size());
    assertEquals("Number of new requirements covered by test 2.", 2, reqs2.size());
    assertEquals("Number of new requirements covered by test 3.", 1, reqs3.size());
    assertEquals("Coverage score", 28, scoreFor(tests));
  }

  @Test
  public void generation() throws Exception {
    ScoreConfiguration config = new ScoreConfiguration();
    config.setLengthWeight(0);
    oc.setTestEndCondition(new LengthProbability(1, 5, 0.1d));
    oc.setTrackOptions(true);
    ReflectiveModelFactory factory = new ReflectiveModelFactory(CalculatorModel.class);
    oc.setFactory(factory);
    GreedyOptimizer optimizer = new GreedyOptimizer(oc, config);
    GenerationResults results = optimizer.search(8);
    List<TestCase> tests = results.getTests();
    assertEquals("Number of tests from greedy", 3, tests.size());
    assertEquals("First test from greedy", "[start, increase, decrease, increase, increase]", tests.get(0).getAllStepNames().toString());
    assertEquals("Second test from greedy", "[start, increase, increase, decrease, decrease]", tests.get(1).getAllStepNames().toString());
    assertEquals("Third test from greedy", "[start, increase, increase, increase, increase]", tests.get(2).getAllStepNames().toString());
//    assertEquals("Output report from greedy", "", TestUtils.getOutput());
    String report = TestUtils.readFile(optimizer.createReportPath(), "UTF8");
    String expected = TestUtils.getResource(GreedyTests.class, "expected-greedy.txt");
    assertEquals("Greedy report", expected, report);
  }

  @Test
  public void timeOut() {
    ScoreConfiguration gc = new ScoreConfiguration();
    gc.setStepWeight(0);
    gc.setStepPairWeight(0);
    gc.setDefaultValueWeight(10);
    gc.setVariableCountWeight(0);
    gc.setRequirementWeight(0);
    oc.setFactory(new ReflectiveModelFactory(RandomValueModel.class));
    oc.setTestEndCondition(new LengthProbability(10, 1d));
    GreedyOptimizer greedy = new GreedyOptimizer(oc, gc);
    greedy.setTimeout(1);
    long start = System.currentTimeMillis();
    greedy.search(100, 8);
    long end = System.currentTimeMillis();
    long diff = end - start;
    assertTrue("Timeout should be 1-3s was "+diff+" ms", diff > 1000 && diff < 3000);
  }

  @Test
  public void defaultFactory() {
    GreedyOptimizer greedy = new GreedyOptimizer(oc, gc);
    oc.setFactory(new SingleInstanceModelFactory());
    TestUtils.startOutputCapture();
    try {
      greedy.search(8);
      fail("Generation without any model objects should fail.");
    } catch (Exception e) {
      //expected
    }
    String output = TestUtils.getOutput();
    assertEquals("Message for default factory", MultiOSMO.ERROR_MSG + System.getProperty("line.separator"), output);
  }

  @Test
  public void maxLength() {
    gc = new ScoreConfiguration();
    oc.setFactory(new ReflectiveModelFactory(RandomValueModel4.class));
    oc.setTestEndCondition(new LengthProbability(5, 1d));
    GreedyOptimizer greedy = new GreedyOptimizer(oc, gc);
    greedy.setMax(2);
    //-1 works because greedy actually produces worse results over time when constrained to max length.. sometimes
    //NOTE: removed -1 since after some fixes it runs forever with this seed
    greedy.setThreshold(0);
    GenerationResults results = greedy.search(100, 8);
    List<TestCase> tests = results.getTests();
    assertEquals("Number of tests from greedy with max", 2, tests.size());
    //asserting the score might be useful as well to see it performs better. but would require better test model.
  }

  @Test
  public void iterationListener() {
    gc = new ScoreConfiguration();
    oc.setFactory(new ReflectiveModelFactory(RandomValueModel4.class));
    oc.setTestEndCondition(new LengthProbability(5, 1d));
    GreedyOptimizer greedy = new GreedyOptimizer(oc, gc);
    MyIterationListener listener = new MyIterationListener();
    greedy.addIterationListener(listener);
    //-1 works because greedy actually produces worse results over time when constrained to max length.. sometimes
    //NOTE: removed -1 since after some fixes it runs forever with this seed
    greedy.setThreshold(0);
    GenerationResults results = greedy.search(100, 8);

    assertEquals("Number of iterations", 17, listener.count);
    assertEquals("Number of iterations", 1, listener.finished);
    assertEquals("Final tests", results.getTests(), listener.finalTests);
    //asserting the score might be useful as well to see it performs better. but would require better test model.
  }

  private static class MyIterationListener implements IterationListener {
    private int count = 0;
    private int finished = 0;
    private List<TestCase> finalTests = null;

    @Override
    public void iterationDone(List<TestCase> tests) {
      count++;
    }

    @Override
    public void generationDone(List<TestCase> tests) {
      finished++;
      finalTests = tests;
    }
  }
}
