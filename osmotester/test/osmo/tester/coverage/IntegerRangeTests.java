package osmo.tester.coverage;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class IntegerRangeTests {
  private ScoreConfiguration config;
  private ScoreCalculator scoreCalculator;

  @Before
  public void setUp() {
    config = new ScoreConfiguration();
    config.setLengthWeight(0);
    config.setPairsWeight(0);
    config.setRequirementWeight(0);
    config.setStepWeight(0);
    config.setDefaultValueWeight(0);
    config.setVariableCountWeight(0);
    scoreCalculator = new ScoreCalculator(config);
  }

  @Test
  public void badRangeArguments() {
    try {
      RangeCategory range = new RangeCategory(null, null);
      //TODO: null output
      fail("Null range input should fail");
    } catch (NullPointerException e) {
      assertEquals("Error msg for null range input name.", "Range name cannot be null.", e.getMessage());
    }
  }

  @Test
  public void invalidInputData() {
    FSM fsm = new FSM();
    TestCase test1 = createTest1(fsm);
    TestStep step = test1.addStep(new FSMTransition("fail"));
    step.addVariableValue("teemu", "problem");
    config.addRange("teemu", "zero", 0, 0);
    config.addRange("teemu", "one", 1, 1);
    config.addRange("teemu", "many", 2, Integer.MAX_VALUE);
    assertEquals("Number of ranges", 1, config.getRanges().size());
    TestCoverage tc = new TestCoverage();
    config.bind(tc);
    try {
      tc.addTestCoverage(test1);
      fail("Wrong type of input to RangeCategory should fail.");
    } catch (RuntimeException e) {
      assertTrue("Type of value", e.getMessage().startsWith("Wrong type"));
    }
  }

  @Test
  public void oneRangeOneValue() {
    config.setRangeWeight("teemu", 3);
    config.addRange("teemu", "zero", 0, 0);
    config.addRange("teemu", "one", 1, 1);
    config.addRange("teemu", "many", 2, Integer.MAX_VALUE);
    TestCoverage tc = new TestCoverage();
    config.bind(tc);

    FSM fsm = new FSM();
    TestCase test1 = createTest1(fsm);
    tc.addTestCoverage(test1);
    assertEquals(3, scoreCalculator.calculateFitness(tc));
  }

  @Test
  public void valueNotInRange() {
    config.setRangeWeight("teemu", 3);
    config.addRange("teemu", "negatives", Integer.MIN_VALUE, -1);
    config.addRange("teemu", "three to five", 3, 4);
    config.addRange("teemu", "plenty", 6, Integer.MAX_VALUE);
    TestCoverage tc = new TestCoverage();
    config.bind(tc);

    FSM fsm = new FSM();
    TestCase test1 = createTest1(fsm);
    tc.addTestCoverage(test1);
    assertEquals(6, scoreCalculator.calculateFitness(tc));
    Map<String, Collection<String>> variables = tc.getVariables();
    Collection<String> ranges = variables.get("teemu-range");
    assertEquals("Number of merged variables", 2, ranges.size());
    assertFalse("Merged range should not contain 'one to three':" + ranges, ranges.contains("one to three"));
    assertTrue("Merged range should contain 'three to five':" + ranges, ranges.contains("three to five"));
    assertTrue("Merged range should contain 'plenty':" + ranges, ranges.contains("plenty"));
  }

  @Test
  public void rangeMergingAll() {
    config.setRangeWeight("teemu", 3);
    config.addRange("teemu", "one to three", 1, 3);
    config.addRange("teemu", "three to five", 3, 5);
    config.addRange("teemu", "plenty", 6, Integer.MAX_VALUE);
    Collection<RangeCategory> calculators = config.getRanges();
    assertEquals("One range should only have one calculator", 1, calculators.size());
    TestCoverage tc = new TestCoverage();
    config.bind(tc);

    FSM fsm = new FSM();
    TestCase test1 = createTest1(fsm);
    tc.addTestCoverage(test1);
    assertEquals(9, scoreCalculator.calculateFitness(tc));
    Map<String, Collection<String>> variables = tc.getVariables();
    Collection<String> ranges = variables.get("teemu-range");
    assertEquals("Number of merged variables", 3, ranges.size());
    assertTrue("Merged range should contain 'one to three':" + ranges, ranges.contains("one to three"));
    assertTrue("Merged range should contain 'three to five':" + ranges, ranges.contains("three to five"));
    assertTrue("Merged range should contain 'plenty':" + ranges, ranges.contains("plenty"));
  }

  private TestCase createTest1(FSM fsm) {
    TestCase test = new TestCase(new TestSuite());
    TestStep step1 = test.addStep(new FSMTransition("my-trans1"));
    step1.addVariableValue("bob", "hi");
    step1.addVariableValue("teemu", 3);
//    step1.storeStateAfter(fsm);

    TestStep step2 = test.addStep(new FSMTransition("my-trans2"));
    step2.addVariableValue("bob", "hi");
    step2.addVariableValue("john", "naughty");
    step2.addVariableValue("teemu", 5);
//    step2.storeStateAfter(fsm);

    TestStep step3 = test.addStep(new FSMTransition("my-trans3"));
    step3.addVariableValue("john", "nice");
    step3.addVariableValue("teemu", 7);
//    step3.storeStateAfter(fsm);
    return test;
  }
}