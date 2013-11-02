package osmo.tester.coverage;

import org.junit.Test;
import osmo.common.log.Logger;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.data.ValueSet;
import osmo.tester.optimizer.GreedyOptimizer;
import osmo.tester.testmodels.CoverageValueModel2;
import osmo.tester.testmodels.RandomValueModel3;
import osmo.tester.testmodels.RandomValueModel4;
import osmo.tester.testmodels.VariableModel1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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
    assertEquals("Number of generated tests", 4, tests.size());
    Map<String, Collection<String>> variables = tc.getValues();
    //1 value = 10
    assertEquals("Variable coverage", "[on paras]", variables.get("teemu").toString());
    //4 values = 40
    assertEquals("Variable coverage", "[null, many, two, one]", variables.get("rangeRange").toString());
    //2 values = 20
    assertEquals("Variable coverage", "[null, many]", variables.get("range2Range").toString());
    //32 values = 320
    assertEquals("Variable coverage", "[paavo&null&null, paavo&many&null, keijo&many&null, keijo&null&null, keijo&two&null, keijo&one&null, teemu&one&null, teemu&many&null, teemu&many&many, keijo&many&many, paavo&many&many, paavo&two&many, teemu&two&many, keijo&two&many, keijo&one&many, teemu&one&many, teemu&null&many, paavo&null&many, keijo&null&many, null&many&null, null&one&null, null&many&many, null&two&many, null&null&many, null&one&many, paavo&one&many, null&null&null, teemu&null&null, teemu&two&null, null&two&null, paavo&two&null, paavo&one&null]",
            variables.get("combo").toString());
    //2 values = 100
    assertEquals("Covered states", "{stateName=[state2, state1]}", tc.getStates().toString());
    //4 values = 160
    assertEquals("Covered state pairs", "{stateName-pair=[state2->state1, state1->state1, state1->state2, state2->state2]}", tc.getStatePairs().toString());
    
    ScoreCalculator sc = new ScoreCalculator(config);
    //se comments above for values
    //10+40+20+320+100+160=70+320+260=70+580=650-98 (steps) = 552
    assertEquals("Coverage score", 552, sc.calculateScore(tc));
  }

  @Test
  public void userState() {
//    Logger.consoleLevel = Level.FINEST;
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
    osmo.addModelClass(CoverageValueModel2.class);
    List<TestCase> tests = osmo.search();
    TestCoverage tc = new TestCoverage(tests);
    ScoreCalculator scorer = new ScoreCalculator(config);
    assertEquals("Number of tests", 6, tests.size());
    assertEquals("Test steps", "TestCase:[t1, t2, t3, t4]", tests.get(0).toString());
    assertEquals("Values covered", "{range=[4, 3, 5]}", tc.getValues().toString());
    //there are 16 state-pairs and 4 states, so it is a total of 24*10
    assertEquals("Coverage score", 176, scorer.calculateScore(tc));
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
    assertEquals("Coverage score", 27, sc.calculateScore(tc));
    assertEquals("Covered values", "[many, one, zero]", tc.getValues().get("rc2").toString());
  }
}
