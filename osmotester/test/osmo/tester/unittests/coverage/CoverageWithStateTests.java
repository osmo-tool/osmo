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
import osmo.tester.optimizer.GreedyOptimizer;
import osmo.tester.unittests.testmodels.CoverageValueModel2;
import osmo.tester.unittests.testmodels.RandomValueModel3;
import osmo.tester.unittests.testmodels.RandomValueModel4;
import osmo.tester.unittests.testmodels.VariableModel1;

import java.util.Collection;
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
    config.ignore("teemu");

   // config.setLengthWeight(0);
    config.setVariableCountWeight(0);
    config.setStepPairWeight(0);
    config.setRequirementWeight(0);
    config.setStepWeight(0);

    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setFactory(new ReflectiveModelFactory(RandomValueModel4.class));
    oc.setTestEndCondition(new LengthProbability(1, 0.1d));
    GreedyOptimizer osmo = new GreedyOptimizer(oc, config);
    GenerationResults results = osmo.search(1000, 33);
    TestCoverage tc = results.getCoverage();
    assertEquals("Number of generated tests", 6, results.getTests().size());
    Map<String, Collection<String>> variables = tc.getVariableValues();
    //1 value = 10
    assertEquals("Variable coverage", "[on paras]", variables.get("teemu").toString());
    //4 values = 40
    assertEquals("Variable coverage", "[null, two, one, many]", variables.get("rangeRange").toString());
    //2 values = 20
    assertEquals("Variable coverage", "[null, many]", variables.get("range2Range").toString());
    //32 values = 320
    assertEquals("Variable coverage", "[keijo&null&null, paavo&null&null, teemu&null&null, teemu&null&many, teemu&two&many, teemu&one&many, paavo&one&many, paavo&null&many, keijo&null&many, paavo&many&many, paavo&two&many, keijo&two&many, keijo&many&many, keijo&one&many, teemu&many&many, null&two&null, keijo&two&null, paavo&two&null, paavo&one&null, keijo&one&null, teemu&one&null, teemu&many&null, null&null&null, null&many&null, null&many&many, null&two&many, null&one&many, null&one&null, keijo&many&null, paavo&many&null, null&null&many, teemu&two&null]",
            variables.get("combo").toString());
    //2 values = 100
    assertEquals("Covered states", "{stateName=[state1, state2]}", tc.getStates().toString());
    //6 values = 240
    assertEquals("Covered state pairs", "{stateName-pair=[osmo.tester.START_STATE->state1, state1->state2, state2->state2, state2->state1, state1->state1, osmo.tester.START_STATE->state2]}", tc.getStatePairs().toString());
    
    ScoreCalculator sc = new ScoreCalculator(config);
    //se comments above for values
    //10+40+20+320+100+240=70+320+340=70+660=710-69 (steps) = 661
    assertEquals("Coverage score", 661, sc.calculateScore(tc));
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
    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setFactory(new ReflectiveModelFactory(CoverageValueModel2.class));
    oc.setTestEndCondition(new LengthProbability(1, 4, 0.1d));
    GreedyOptimizer osmo = new GreedyOptimizer(oc, config);
    GenerationResults results = osmo.search(155);
    TestCoverage tc = results.getCoverage();
    Collection<TestCase> tests = results.getTests();
    ScoreCalculator scorer = new ScoreCalculator(config);
    assertEquals("Number of tests", 6, tests.size());
    assertEquals("Test steps", "TestCase:[t1, t2, t3, t4]", tests.iterator().next().toString());
    assertEquals("Values covered", "{range=[4, 1, 5, 2]}", tc.getVariableValues().toString());
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
