package osmo.tester.unittests.optimizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;
import osmo.tester.optimizer.greedy.MultiGreedy;
import osmo.tester.unittests.testmodels.CalculatorModel;

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

  private static class MyModelFactory implements ModelFactory {
    @Override
    public void createModelObjects(TestModels addHere) {
      addHere.add(new CalculatorModel());
    }
  }
}
