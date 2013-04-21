package osmo.tester.coverage;

import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.GreedyOptimizer;
import osmo.tester.testmodels.RandomValueModel2;
import osmo.tester.testmodels.RandomValueModel3;
import osmo.tester.testmodels.StateDescriptionModel2;
import osmo.tester.testmodels.VariableModel1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class CoverageWithStateTests {
  @Test
  public void combineWithCustomAndStateVariables() {
    OSMOConfiguration.setSeed(55);
    ScoreConfiguration config = new ScoreConfiguration();
    config.setDefaultValueWeight(0);
    config.disableCheckingFor("teemu");
    config.addOneTwoManyRange("range", 0);
    config.addZeroOneManyRange("range2", 0);

    config.setLengthWeight(0);
    config.setVariableCountWeight(0);
    config.setStepPairWeight(0);
    config.setRequirementWeight(0);
    config.setStepWeight(0);
    config.addCombination(1, "names", "range-range", "range2-range");
    GreedyOptimizer osmo = new GreedyOptimizer(config, new LengthProbability(1, 0.1d));
    osmo.addModelClass(RandomValueModel2.class);
    List<TestCase> tests = osmo.search();
    assertEquals("Number of generated tests", 1, tests.size());
    Map<String,ModelVariable> variables = tests.get(0).getStepVariables();
    assertEquals("Variable coverage", "teemu([on paras])", variables.get("teemu").toString());
    assertEquals("Variable coverage", "range-range([one, many, many, one, many, many, two, many])", 
            variables.get("range-range").toString());
    assertEquals("Variable coverage", "range2-range([many, many, many, many, many, many, many, many])", 
            variables.get("range2-range").toString());
    assertEquals("Variable coverage", "names&range-range&range2-range([null&one&null, null&null&null, null&many&null, null&many&null, null&null&many, null&null&many, paavo&null&null, null&null&null, null&null&many, null&null&many, null&one&null, teemu&null&null, null&many&null, null&null&many, keijo&null&null, null&null&null, null&null&many, null&null&null, teemu&null&null, null&many&null, null&two&null, null&null&many, teemu&null&null, null&many&null, null&null&many])", 
            variables.get("names&range-range&range2-range").toString());    
  }

  @Test
  public void userState() {
    OSMOConfiguration.setSeed(155);
    ScoreConfiguration config = new ScoreConfiguration();
    config.setDefaultValueWeight(0);
    config.setStateWeight(10);
    config.setStatePairWeight(10);
    config.setLengthWeight(0);
    config.setVariableCountWeight(0);
    config.setStepPairWeight(0);
    config.setRequirementWeight(0);
    config.setStepWeight(0);
    GreedyOptimizer osmo = new GreedyOptimizer(config, new LengthProbability(1, 4, 0.1d));
    osmo.addModelClass(StateDescriptionModel2.class);
    List<TestCase> tests = osmo.search();
    TestCoverage tc = new TestCoverage(tests);
    ScoreCalculator scorer = new ScoreCalculator(config);
    assertEquals("Number of tests", 6, tests.size());
    assertEquals("Test steps" , "TestCase:[t1, t2, t3, t4]", tests.get(0). toString());
    assertEquals("States covered", "[1, 2, 3, 4]", tc.getStates().toString());
    List<String> statePairs = new ArrayList<>();
    statePairs.addAll(tc.getStatePairs());
    Collections.sort(statePairs);
    assertEquals("State-Pairs covered", "[1->1, 1->2, 1->3, 1->4, 2->1, 2->2, 2->3, 2->4, 3->1, 3->2, 3->3, 3->4, 4->1, 4->2, 4->3, 4->4, osmo.start.state->1, osmo.start.state->2, osmo.start.state->3, osmo.start.state->4]", statePairs.toString());
    //there are 20 state-pairs and 4 states, so it is a total of 24*10
    assertEquals("Coverage score", 240, scorer.calculateScore(tc));
  }

  @Test
  public void wrongVariables() {
    ScoreConfiguration config = new ScoreConfiguration();
    OSMOConfiguration.setSeed(55);
    config.addCombination(5, "i1", "i2", "j7", "q8");
    config.addRange("h5", "i1", 0, 0);
    config.setRangeWeight("h5", 6);
    GreedyOptimizer greedy = new GreedyOptimizer(config, new LengthProbability(1, 0.2d));
    greedy.addModelClass(VariableModel1.class);
    try {
      List<TestCase> tests = greedy.search();
    } catch (IllegalArgumentException e) {
      String expected = "Following coverage variables not found in the model:[h5, j7, q8]";
      assertEquals("Error message for unknown variable names", expected, e.getMessage());
    }
  }

  @Test
  public void stepValues() {
    OSMOConfiguration.setSeed(55);
    ScoreConfiguration config = new ScoreConfiguration();
    config.setDefaultValueWeight(10);

    config.setLengthWeight(0);
    config.setVariableCountWeight(0);
    config.setStepPairWeight(0);
    config.setRequirementWeight(0);
    config.setStepWeight(0);
    GreedyOptimizer osmo = new GreedyOptimizer(config, 100, new LengthProbability(1, 0.1d));
    osmo.addModelClass(RandomValueModel3.class);
    List<TestCase> tests = osmo.search();
//    OSMOTester osmo = new OSMOTester(new RandomValueModel3());
//    osmo.generate();
//    List<TestCase> tests = osmo.getSuite().getAllTestCases();
    assertEquals("Number of generated tests", 1, tests.size());
    TestCoverage tc = new TestCoverage();
    tc.addTestCoverage(tests.get(0));
    ScoreCalculator sc = new ScoreCalculator(config);
    assertEquals("Coverage score", 30, sc.calculateScore(tc));
    assertEquals("Covered values", "[many, zero, one]", tc.getVariables().get("rc2").toString());
  }
}
