package osmo.tester.coverage;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class CombinationCoverageTests {
  private ScoreConfiguration config;
  private ScoreCalculator scoreCalculator;

  @Before
  public void setUp() {
    config = new ScoreConfiguration();
    config.setLengthWeight(0);
    config.setStepPairWeight(0);
    config.setRequirementWeight(0);
    config.setStepWeight(0);
    config.setDefaultValueWeight(0);
    config.setVariableCountWeight(0);
    scoreCalculator = new ScoreCalculator(config);
  }

  @Test
  public void badCombinationArguments() {
    try {
      CombinationCoverage combo = new CombinationCoverage("one", "one", "tree", "four");
      fail("Multiple instances of same input name should fail.");
    } catch (IllegalArgumentException e) {
      //expected
    }
    try {
      CombinationCoverage combo = new CombinationCoverage(null, "one", "tree", "four");
      fail("Null input name should fail.");
    } catch (NullPointerException e) {
      assertEquals("Error msg for null input", "Input name cannot be null.", e.getMessage());
    }
    try {
      CombinationCoverage combo = new CombinationCoverage();
      fail("No input name should fail.");
    } catch (IllegalArgumentException e) {
      assertEquals("Error msg for no input", "You must specify some input variables to combine.", e.getMessage());
    }
  }

  @Test
  public void completelyMissingData() {
    FSM fsm = new FSM();
    TestCase test1 = createTest1(fsm);
    config.addCombination(5, "pesu", "sieni");
    TestCoverage tc = new TestCoverage();
    config.bind(tc);
    tc.addTestCoverage(test1);
    int score = scoreCalculator.calculateScore(tc);
    //there is one value in all steps: all values are null, and it should not be considered
    assertEquals("Coverage with missing data", 5, score);
  }

  @Test
  public void partlyMissingData() {
    FSM fsm = new FSM();
    TestCase test1 = createTest1(fsm);
    config.addCombination(7, "bob", "teemu", "jaska");
    config.addCombination(7, "bob", "teemu");
    config.addCombination(10, "teemu", "jaska");
    TestCoverage tc = new TestCoverage();
    config.bind(tc);
    tc.addTestCoverage(test1);
    int actual = scoreCalculator.calculateScore(tc);
    //we have three values for bob&teemu&jaska, three values for teemu&jaska, three values for bob&teemu
    //3*7+3*10+3*7=21+30+21=72
    assertEquals("Partial coverage score", 72, actual);
  }

  @Test
  public void calculate() {
    FSM fsm = new FSM();
    TestCase test1 = createTest1(fsm);
    config.setDefaultValueWeight(0);
    config.addCombination(3, "bob", "teemu");
    config.addCombination(8, "john", "teemu");
    config.addCombination(11, "john", "bob", "teemu");
    config.addCombination(1, "john", "undefined", "bob", "teemu");
    config.addCombination(1, "undefined", "unknown", "nothing", "nosepick");
    TestCoverage tc = new TestCoverage();
    config.bind(tc);
    tc.addTestCoverage(test1);
    int actual = scoreCalculator.calculateScore(tc);
    //3xbob&teemu = 3x3 = 9
    //3xjohn&teemu = 3x8 = 24
    //3xbob&john&teemu = 3x11 = 33
    //3xbob&john&teemu&undefined = 3x1 = 3
    //1xnosepick&nothing&undefined&unknown = 1x1 = 1
    //total = 9+24+33+3+1 ) 10+24+36 = 70
    assertEquals("Combination coverage score", 70, actual);
  }
  
  private TestCase createTest1(FSM fsm) {
    TestCase test = new TestCase(new TestSuite());
    TestStep step1 = test.addStep(new FSMTransition("my-trans1"));
    step1.addVariableValue("bob", "hi");
    step1.addVariableValue("teemu", 3);
    step1.storeGeneralState(fsm);

    TestStep step2 = test.addStep(new FSMTransition("my-trans2"));
    step2.addVariableValue("bob", "hi");
    step2.addVariableValue("john", "naughty");
    step2.addVariableValue("teemu", 5);
    step1.storeGeneralState(fsm);

    TestStep step3 = test.addStep(new FSMTransition("my-trans3"));
    step3.addVariableValue("john", "nice");
    step3.addVariableValue("teemu", 7);
    step1.storeGeneralState(fsm);
    return test;
  }

  @Test
  public void withStates() {
    FSM fsm = new FSM();
    TestCase test1 = createTest2(fsm);
    config.addCombination(1, "bob", "teemu", "john");
    TestCoverage tc = new TestCoverage();
    config.bind(tc);
    tc.addTestCoverage(test1);
    int actual = scoreCalculator.calculateScore(tc);
    //there are 3 states and 8 unique value combos = 50*3+8 = 158
    //there are also 5 state-pairs, adding 40*5 = 200 for a total of 358
    assertEquals("Partial coverage score", 318, actual);
  }

  private TestCase createTest2(FSM fsm) {
    TestCase test = new TestCase(new TestSuite());
    TestStep step1 = test.addStep(new FSMTransition("my-step1"));
    step1.setUserState("state1");
    step1.addVariableValue("bob", "hi");
    step1.addVariableValue("teemu", 3);

    TestStep step2 = test.addStep(new FSMTransition("my-step2"));
    step2.setUserState("state2");
    step2.addVariableValue("bob", "hiho");
    step2.addVariableValue("john", "naughty");
    step2.addVariableValue("teemu", 5);
    step2.addVariableValue("teemu", 6);

    TestStep step3 = test.addStep(new FSMTransition("my-step3"));
    step3.setUserState("state3");
    step3.addVariableValue("john", "nice");
    step3.addVariableValue("teemu", 7);

    TestStep step4 = test.addStep(new FSMTransition("my-step4"));
    step4.setUserState("state2");
    step4.addVariableValue("john", "nice");
    step4.addVariableValue("teemu", 9);
    step4.addVariableValue("teemu", 22);

    TestStep step5 = test.addStep(new FSMTransition("my-step5"));
    step5.setUserState("state3");
    step5.addVariableValue("bob", "pip");
    step5.addVariableValue("john", "fishy");
    step5.addVariableValue("teemu", 77);

    TestStep step6 = test.addStep(new FSMTransition("my-step6"));
    step6.setUserState(null);
    step6.addVariableValue("john", "fishier");
    step6.addVariableValue("teemu", 777);
    return test;
  }
}
