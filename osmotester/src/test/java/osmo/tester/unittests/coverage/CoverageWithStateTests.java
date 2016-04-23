package osmo.tester.unittests.coverage;

import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.CombinationCoverage;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.data.ValueSet;
import osmo.tester.optimizer.GenerationResults;
import osmo.tester.optimizer.greedy.GreedyOptimizer;
import osmo.tester.unittests.testmodels.CoverageValueModel2;
import osmo.tester.unittests.testmodels.RandomValueModel3;
import osmo.tester.unittests.testmodels.RandomValueModel4;
import osmo.tester.unittests.testmodels.VariableModel1;

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
    //Logger.consoleLevel = Level.INFO;
    ScoreConfiguration config = new ScoreConfiguration();
    config.setDefaultValueWeight(10);
    config.ignore("teemu");

    //config.setLengthWeight(0);
    config.setVariableCountWeight(0);
    config.setStepPairWeight(0);
    config.setRequirementWeight(0);
    config.setStepWeight(0);

    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setFactory(new ReflectiveModelFactory(RandomValueModel4.class));
    oc.setTestEndCondition(new LengthProbability(1, 0.1d));
    GreedyOptimizer osmo = new GreedyOptimizer(oc, config);
    GenerationResults results = osmo.search(2000, 333);
    TestCoverage tc = results.getCoverage();
    assertEquals("Number of generated tests", 5, results.getTests().size());
    Map<String, Collection<String>> variables = tc.getVariableValues();
    //1 value = 10
    assertEquals("Variable coverage", "[on paras]", variables.get("teemu").toString());
    //4 values = 40
    assertEquals("Variable coverage", "[many, one, null, two]", variables.get("rangeRange").toString());
    //2 values = 20
    assertEquals("Variable coverage", "[null, many]", variables.get("range2Range").toString());
    //32 values = 320
    List<String> combo = new ArrayList<>();
    combo.addAll(variables.get("combo"));
    Collections.sort(combo);
    assertEquals("Variable coverage", "[keijo&many&many, keijo&many&null, keijo&null&many, keijo&null&null, keijo&one&many, keijo&one&null, keijo&two&many, keijo&two&null, null&many&many, null&many&null, null&null&many, null&null&null, null&one&many, null&one&null, null&two&many, null&two&null, paavo&many&many, paavo&many&null, paavo&null&many, paavo&null&null, paavo&one&many, paavo&one&null, paavo&two&many, paavo&two&null, teemu&many&many, teemu&many&null, teemu&null&many, teemu&null&null, teemu&one&many, teemu&one&null, teemu&two&many, teemu&two&null]",
            combo.toString());
    //2 values = 100
    assertEquals("Covered states", "{stateName=[state2, state1]}", tc.getStates().toString());
    //6 values = 240
    assertEquals("Covered state pairs", "{stateName-pair=[osmo.tester.START_STATE->state2, state2->state1, state1->state1, state1->state2, state2->state2, osmo.tester.START_STATE->state1]}", tc.getStatePairs().toString());
    
    ScoreCalculator sc = new ScoreCalculator(config);
    //see comments above for values
    //10+40+20+320+100+240=70+320+340=70+660=710-76 (steps) = 654
    assertEquals("Coverage score", 654, sc.calculateScore(tc));
  }
//  Expected :[
//  keijo&many&many, keijo&many&null, keijo&null&many, keijo&null&null, keijo&one&many, keijo&two&many, keijo&two&null, --keijo&one&null,
//          null&many&many, null&many&null, null&null&many, null&null&null, null&one&many, --null&one&null, --null&two&many, --null&two&null,
//  paavo&many&many, --paavo&many&null, --paavo&null&null, paavo&null&many, paavo&one&many, --paavo&one&null, paavo&two&many, --paavo&two&null,
//  teemu&many&many, teemu&many&null, teemu&null&many, teemu&null&null, teemu&one&many, teemu&two&many, teemu&one&null, --teemu&two&null
//
//  Actual   :[
//  keijo&many&many, keijo&many&null, keijo&null&many, keijo&null&null, keijo&one&many, keijo&two&many, keijo&two&null,
//          null&many&many, null&many&null, null&null&many, null&null&null, null&one&many,
//  paavo&many&many, paavo&null&many, paavo&one&many, paavo&two&many,
//  teemu&many&many, teemu&many&null, teemu&null&many, teemu&null&null, teemu&one&many, teemu&two&many, teemu&two&null]

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
    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setFactory(new ReflectiveModelFactory(CoverageValueModel2.class));
    oc.setTestEndCondition(new LengthProbability(1, 4, 0.1d));
    GreedyOptimizer osmo = new GreedyOptimizer(oc, config);
    GenerationResults results = osmo.search(156);
    TestCoverage tc = results.getCoverage();
    Collection<TestCase> tests = results.getTests();
    ScoreCalculator scorer = new ScoreCalculator(config);
    assertEquals("Number of tests", 6, tests.size());
    assertEquals("Test steps", "TestCase:[t1, t2, t3, t4]", tests.iterator().next().toString());
    assertEquals("Values covered", "{range=[1, 3, 4]}", tc.getVariableValues().toString());
//    //there are 16 state-pairs and 4 states, so it is a total of 24*10
    assertEquals("Coverage score", 240, scorer.calculateScore(tc));
  }

  @Test
  public void wrongVariables() {
    ScoreConfiguration config = new ScoreConfiguration();
    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setFactory(new ReflectiveModelFactory(VariableModel1.class));
    oc.setTestEndCondition( new LengthProbability(1, 0.2d));
    GreedyOptimizer greedy = new GreedyOptimizer(oc, config);
    try {
      greedy.search(55);
    } catch (IllegalArgumentException e) {
      String expected = "Following coverage variables not found in the model:[j7, q8]";
      assertEquals("Error message for unknown variable names", expected, e.getMessage());
    }
  }

  @Test
  public void stepValues() {
    ScoreConfiguration config = new ScoreConfiguration();
    config.setDefaultValueWeight(10);

//    config.setLengthWeight(-1);
    config.setVariableCountWeight(0);
    config.setStepPairWeight(0);
    config.setRequirementWeight(0);
    config.setStepWeight(0);
    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setFactory(new ReflectiveModelFactory(RandomValueModel3.class));
    oc.setTestEndCondition( new LengthProbability(1, 0.1d));
    GreedyOptimizer osmo = new GreedyOptimizer(oc, config);
    GenerationResults results = osmo.search(100, 55);
    assertEquals("Number of generated tests", 1, results.getTests().size());
    TestCoverage tc = new TestCoverage();
    tc.addCoverage(results.getTests().get(0).getCoverage());
    ScoreCalculator sc = new ScoreCalculator(config);
    assertEquals("Coverage score", 27, sc.calculateScore(tc));
    assertEquals("Covered values", "[many, one, zero]", tc.getVariableValues().get("rc2").toString());
  }
}
