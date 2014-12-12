package osmo.tester.unittests.optimizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;
import osmo.tester.optimizer.greedy.IterationListener;
import osmo.tester.optimizer.greedy.MultiGreedy;
import osmo.tester.unittests.testmodels.CalculatorModel;
import osmo.tester.unittests.testmodels.RandomValueModel4;

import java.util.List;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class MultiGreedyTests {
  @Before
  public void initTest() {
    TestUtils.startOutputCapture();
  }

  @After
  public void resetAfter() {
    TestUtils.endOutputCapture();
  }
  
  @Test
  public void calculatorOptimization() throws Exception {
    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setTestEndCondition(new LengthProbability(1, 5, 0.2d));
    oc.setFactory(new MyModelFactory());
    ScoreConfiguration config = new ScoreConfiguration();
    config.setLengthWeight(0);
    MultiGreedy multiGreedy = new MultiGreedy(oc, config, 111, 5);
    List<TestCase> tests = multiGreedy.search();
    assertEquals("Number of tests from MultiGreedy", 3, tests.size());
    assertEquals("MultiGreedy test1", "TestCase:[start, increase, decrease, increase, increase]", tests.get(0).toString());
    assertEquals("MultiGreedy test2", "TestCase:[start, increase, increase, decrease, decrease]", tests.get(1).toString());
    assertEquals("MultiGreedy test3", "TestCase:[start, increase, increase, increase, increase]", tests.get(2).toString());
    String report = TestUtils.readFile(multiGreedy.createFinalReportPath(), "UTF8");
    String expected = TestUtils.getResource(GreedyTests.class, "expected-multigreedy.txt");
    assertEquals("Multi-Greedy report", expected, report);  
    }
  
  @Test
  public void maxLength() {
    OSMOConfiguration oc = new OSMOConfiguration();
    ScoreConfiguration sc = new ScoreConfiguration();
    oc.setFactory(new ReflectiveModelFactory(RandomValueModel4.class));
    oc.setTestEndCondition(new LengthProbability(5, 1d));
    MultiGreedy greedy = new MultiGreedy(oc, sc, 1);
    greedy.setMax(2);
    greedy.setTimeout(2);
    greedy.setThreshold(-1);
    List<TestCase> tests = greedy.search();
    assertEquals("Number of tests from greedy with max", 2, tests.size());
    //asserting the score might be useful as well to see it performs better. but would require better test model.
  }

  @Test
  public void iterationListener() {
    OSMOConfiguration oc = new OSMOConfiguration();
    ScoreConfiguration sc = new ScoreConfiguration();
    oc.setFactory(new ReflectiveModelFactory(RandomValueModel4.class));
    oc.setTestEndCondition(new LengthProbability(5, 1d));
    MultiGreedy greedy = new MultiGreedy(oc, sc, 1);
    greedy.setTimeout(2);
    greedy.setThreshold(-1);
    MyIterationListener listener = new MyIterationListener();
    greedy.addIterationListener(listener);
    List<TestCase> tests = greedy.search();
//    assertEquals("Number of iterations", 2, listener.count);
    assertEquals("Number of iterations", 1, listener.finished);
    assertEquals("Final tests", tests, listener.finalTests);
  }

  private static class MyModelFactory implements ModelFactory {
    @Override
    public void createModelObjects(TestModels addHere) {
      addHere.add(new CalculatorModel());
    }
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
