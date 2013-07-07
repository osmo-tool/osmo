package osmo.tester.coverage;

import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.data.ValueSet;
import osmo.tester.optimizer.GreedyOptimizer;
import osmo.tester.testmodels.RandomValueModel3;
import osmo.tester.testmodels.RandomValueModel4;
import osmo.tester.testmodels.StateDescriptionModel2;
import osmo.tester.testmodels.VariableModel1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class CoverageWithStateTests {
  @Test
  public void badCombinationArguments() {
    try {
      CombinationCoverage combo = new CombinationCoverage(null);
      fail("Combination with null input should fail.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error msg for null input", "You must specify some input variables to combine.", e.getMessage());
    }
    try {
      CombinationCoverage combo = new CombinationCoverage();
      fail("Zero inputs should fail.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error msg for null input", "You must specify some input variables to combine.", e.getMessage());
    }
    try {
      ValueSet<String> vs = new ValueSet<>();
      vs.setName("test-variable");
      CombinationCoverage combo = new CombinationCoverage(vs, vs);
      fail("Duplicate inputs should fail");
    } catch (IllegalArgumentException e) {
      assertEquals("Error msg for no input", "Variable only allowed once in combination:ValueSet{name=test-variable, options=[]}", e.getMessage());
    }
  }

  @Test
  public void combineWithCustomAndStateVariables() {
    ScoreConfiguration config = new ScoreConfiguration();
    config.setDefaultValueWeight(10);
    config.disableCheckingFor("teemu");

    config.setLengthWeight(0);
    config.setVariableCountWeight(0);
    config.setStepPairWeight(0);
    config.setRequirementWeight(0);
    config.setStepWeight(0);
    
    GreedyOptimizer osmo = new GreedyOptimizer(config, 1000, new LengthProbability(1, 0.1d), 33);
    osmo.addModelClass(RandomValueModel4.class);
    List<TestCase> tests = osmo.search();
    TestCoverage tc = new TestCoverage();
    for (TestCase test : tests) {
      tc.addTestCoverage(test);
    }
    assertEquals("Number of generated tests", 5, tests.size());
    Map<String, Collection<String>> variables = tc.getValues();
    //1 value = 10
    assertEquals("Variable coverage", "[on paras]", variables.get("teemu").toString());
    //4 values = 40
    assertEquals("Variable coverage", "[null, two, one, many]", variables.get("rangeRange").toString());
    //2 values = 20
    assertEquals("Variable coverage", "[null, many]", variables.get("range2Range").toString());
    //32 values = 320
    assertEquals("Variable coverage", "[keijo&null&null, paavo&null&null, teemu&null&null, teemu&null&many, teemu&two&many, teemu&one&many, paavo&one&many, paavo&null&many, keijo&null&many, paavo&many&many, paavo&two&many, keijo&two&many, keijo&many&many, keijo&one&many, teemu&many&many, null&two&null, keijo&two&null, paavo&two&null, paavo&one&null, keijo&one&null, teemu&one&null, teemu&many&null, null&many&null, null&one&null, null&many&many, null&two&many, null&null&many, null&one&many, null&null&null, keijo&many&null, paavo&many&null, teemu&two&null]",
            variables.get("combo").toString());
    //2 values = 100
    assertEquals("Covered states", "[state1, state2]", tc.getStates().toString());
    //6 values = 240
    assertEquals("Covered state pairs", "[osmo.start.state->state1, state1->state2, state2->state2, state2->state1, state1->state1, osmo.start.state->state2]", tc.getStatePairs().toString());
    
    ScoreCalculator sc = new ScoreCalculator(config);
    //10+40+20+320+100+240=70+320+340=70+660=730
    assertEquals("Coverage score", 730, sc.calculateScore(tc));
  }

  @Test
  public void userState() {
    ScoreConfiguration config = new ScoreConfiguration();
    config.setDefaultValueWeight(0);
    config.setStateWeight(10);
    config.setStatePairWeight(10);
    config.setLengthWeight(0);
    config.setVariableCountWeight(0);
    config.setStepPairWeight(0);
    config.setRequirementWeight(0);
    config.setStepWeight(0);
    GreedyOptimizer osmo = new GreedyOptimizer(config, new LengthProbability(1, 4, 0.1d), 155);
    osmo.addModelClass(StateDescriptionModel2.class);
    List<TestCase> tests = osmo.search();
    TestCoverage tc = new TestCoverage(tests);
    ScoreCalculator scorer = new ScoreCalculator(config);
    assertEquals("Number of tests", 6, tests.size());
    assertEquals("Test steps", "TestCase:[t1, t2, t3, t4]", tests.get(0).toString());
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
    GreedyOptimizer greedy = new GreedyOptimizer(config, new LengthProbability(1, 0.2d), 55);
    greedy.addModelClass(VariableModel1.class);
    try {
      List<TestCase> tests = greedy.search();
    } catch (IllegalArgumentException e) {
      String expected = "Following coverage variables not found in the model:[j7, q8]";
      assertEquals("Error message for unknown variable names", expected, e.getMessage());
    }
  }

  @Test
  public void stepValues() {
    ScoreConfiguration config = new ScoreConfiguration();
    config.setDefaultValueWeight(10);

    config.setLengthWeight(0);
    config.setVariableCountWeight(0);
    config.setStepPairWeight(0);
    config.setRequirementWeight(0);
    config.setStepWeight(0);
    GreedyOptimizer osmo = new GreedyOptimizer(config, 100, new LengthProbability(1, 0.1d), 55);
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
    assertEquals("Covered values", "[one, many, zero]", tc.getValues().get("rc2").toString());
  }
}
