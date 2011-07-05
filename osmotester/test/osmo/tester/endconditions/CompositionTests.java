package osmo.tester.endconditions;

import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.examples.CalculatorModel;
import osmo.tester.generator.endcondition.AndComposition;
import osmo.tester.generator.endcondition.LengthCondition;
import osmo.tester.generator.endcondition.OrComposition;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class CompositionTests {
  @Test
  public void length2AndLength4() {
    LengthCondition length2 = new LengthCondition(2);
    LengthCondition length4 = new LengthCondition(4);
    AndComposition and = new AndComposition(length2, length4);

    CalculatorModel calculator = new CalculatorModel();
    OSMOTester tester = new OSMOTester(calculator);
    tester.addTestEndCondition(and);
    tester.addSuiteEndCondition(length2);
    tester.generate();
    TestSuite testLog = calculator.getHistory();
    List<TestCase> history = testLog.getHistory();
    assertEquals("Number of tests generated", 2, history.size());
    for (TestCase test : history) {
      assertEquals("Number of steps in a test case", 4, test.getSteps().size());
    }
  }

  @Test
  public void length2AndLength4AndLength6() {
    LengthCondition length2 = new LengthCondition(2);
    LengthCondition length4 = new LengthCondition(4);
    LengthCondition length6 = new LengthCondition(6);
    AndComposition and = new AndComposition(length2, length4, length6);

    CalculatorModel calculator = new CalculatorModel();
    OSMOTester tester = new OSMOTester(calculator);
    tester.addTestEndCondition(and);
    tester.addSuiteEndCondition(length2);
    tester.generate();
    TestSuite testLog = calculator.getHistory();
    List<TestCase> history = testLog.getHistory();
    assertEquals("Number of tests generated", 2, history.size());
    for (TestCase test : history) {
      assertEquals("Number of steps in a test case", 6, test.getSteps().size());
    }
  }

  @Test
  public void length2OrLength4OrLength6() {
    LengthCondition length2 = new LengthCondition(2);
    LengthCondition length4 = new LengthCondition(4);
    LengthCondition length6 = new LengthCondition(6);
    OrComposition or = new OrComposition(length2, length4, length6);

    CalculatorModel calculator = new CalculatorModel();
    OSMOTester tester = new OSMOTester(calculator);
    tester.addTestEndCondition(or);
    tester.addSuiteEndCondition(length2);
    tester.generate();
    TestSuite testLog = calculator.getHistory();
    List<TestCase> history = testLog.getHistory();
    assertEquals("Number of tests generated", 2, history.size());
    for (TestCase test : history) {
      assertEquals("Number of steps in a test case", 2, test.getSteps().size());
    }
  }

  @Test
  public void suiteLength2AndLength4AndLength6() {
    LengthCondition length2 = new LengthCondition(2);
    LengthCondition length4 = new LengthCondition(4);
    LengthCondition length6 = new LengthCondition(6);
    AndComposition and = new AndComposition(length2, length4, length6);

    CalculatorModel calculator = new CalculatorModel();
    OSMOTester tester = new OSMOTester(calculator);
    tester.addTestEndCondition(and);
    tester.addSuiteEndCondition(and);
    tester.generate();
    TestSuite testLog = calculator.getHistory();
    List<TestCase> history = testLog.getHistory();
    assertEquals("Number of tests generated", 6, history.size());
    for (TestCase test : history) {
      assertEquals("Number of steps in a test case", 6, test.getSteps().size());
    }
  }
}
