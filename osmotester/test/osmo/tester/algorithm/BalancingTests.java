package osmo.tester.algorithm;

import org.junit.Before;
import org.junit.Test;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generation.TestSequenceListener;
import osmo.tester.generator.algorithm.BalancingAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.testmodels.ValidTestModel6;
import osmo.tester.testmodels.ValidTestModel7;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class BalancingTests {
  private OSMOTester osmo = null;
  private TestSequenceListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    listener = new TestSequenceListener();
    osmo.addListener(listener);
  }

  @Test
  public void stepsOnly() {
    for (int i = 0 ; i < 1000 ; i++) {
      OSMOTester tester = new OSMOTester();
      tester.setAlgorithm(new BalancingAlgorithm());
      tester.addModelObject(new ValidTestModel6());
      tester.setTestEndCondition(new Length(4));
      tester.setSuiteEndCondition(new Length(1));
      tester.generate(i);
      assertStepsFound(tester.getSuite(), "t1", "t2", "t3", "t4");
    }
  }

  private void assertStepsFound(TestSuite suite, String... steps) {
    TestCoverage tc = new TestCoverage(suite.getAllTestCases());
    Collection<String> singles = tc.getSingles();
    for (String step : steps) {
      assertTrue("Test should contain step " + step, singles.contains(step));
    }
  }

  @Test
  public void statistics() {
    List<String> pairs = createPairList("t1", "t2", "t3", "t4");
    OSMOTester tester = new OSMOTester();
    tester.setAlgorithm(new BalancingAlgorithm());
    tester.addModelObject(new ValidTestModel6());
    tester.setTestEndCondition(new Length(2500));
    tester.setSuiteEndCondition(new Length(4));
    tester.generate(55);

    Map<String, Integer> counts = new HashMap<>();
    List<TestCase> tests = tester.getSuite().getAllTestCases();
    List<String> starters = new ArrayList<>();
    for (TestCase test : tests) {
      String previous = FSM.START_NAME;
      starters.add(test.getSteps().get(0).getName());
      for (TestCaseStep step : test.getSteps()) {
        String name = step.getName();
        String pair = previous + "->" + name;
        previous = name;
        Integer count = counts.get(pair);
        if (count == null) count = 0;
        counts.put(pair, ++count);
      }
    }
    
    assertEquals("Number of pairs", 20, counts.size());

    for (String pair : pairs) {
      int count = counts.get(pair);
      int avg = 2500 * 4 / 16;
      int threshold = 5;
      int min = avg - threshold;
      int max = avg + threshold;
      if (count < min || count > max) {
        if (pair.startsWith(FSM.START_NAME)) {
          assertEquals("Start pair count for "+pair, 1, count);
          continue;
        }
        String msg = "Step pair count should be between " + min + " and " + max + ". " + pair + " was " + count + ".";
        msg += " All pairs:" + counts;
        fail(msg);
      }
    }
    assertTrue("t1 should start a test", starters.contains("t1"));
    assertTrue("t2 should start a test", starters.contains("t2"));
    assertTrue("t3 should start a test", starters.contains("t3"));
    assertTrue("t4 should start a test", starters.contains("t4"));
  }

  private List<String> createPairList(String... steps) {
    List<String> list = new ArrayList<>();
    for (String source : steps) {
      for (String target : steps) {
        list.add(source + "->" + target);
      }
    }
    for (String step : steps) {
      list.add(FSM.START_NAME + "->" + step);
    }
    return list;
  }
}
