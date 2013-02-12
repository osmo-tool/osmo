package osmo.tester.endconditions;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.And;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Or;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.testmodels.CalculatorModel;

import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class CompositionTests {
  @Before
  public void setSeed() {
    OSMOConfiguration.setSeed(333);
  }
  
  @Test
  public void length2AndLength4() {
    Length length2 = new Length(2);
    Length length4 = new Length(4);
    And and = new And(length2, length4);

    CalculatorModel calculator = new CalculatorModel();
    OSMOTester tester = new OSMOTester(calculator);
    tester.addTestEndCondition(and);
    tester.addSuiteEndCondition(length2);
    tester.generate();
    TestSuite testLog = calculator.getHistory();
    List<TestCase> history = testLog.getFinishedTestCases();
    assertEquals("Number of tests generated", 2, history.size());
    for (TestCase test : history) {
      assertEquals("Number of steps in a test case", 4, test.getSteps().size());
    }
  }

  @Test
  public void length2AndLength4AndLength6() {
    Length length2 = new Length(2);
    Length length4 = new Length(4);
    Length length6 = new Length(6);
    And and = new And(length2, length4, length6);

    CalculatorModel calculator = new CalculatorModel();
    OSMOTester tester = new OSMOTester(calculator);
    tester.addTestEndCondition(and);
    tester.addSuiteEndCondition(length2);
    tester.generate();
    TestSuite testLog = calculator.getHistory();
    List<TestCase> history = testLog.getFinishedTestCases();
    assertEquals("Number of tests generated", 2, history.size());
    for (TestCase test : history) {
      assertEquals("Number of steps in a test case", 6, test.getSteps().size());
    }
  }

  @Test
  public void length2OrLength4OrLength6() {
    Length length2 = new Length(2);
    Length length4 = new Length(4);
    Length length6 = new Length(6);
    Or or = new Or(length2, length4, length6);

    CalculatorModel calculator = new CalculatorModel();
    OSMOTester tester = new OSMOTester(calculator);
    tester.addTestEndCondition(or);
    tester.addSuiteEndCondition(length2);
    tester.generate();
    TestSuite testLog = calculator.getHistory();
    List<TestCase> history = testLog.getFinishedTestCases();
    assertEquals("Number of tests generated", 2, history.size());
    for (TestCase test : history) {
      assertEquals("Number of steps in a test case", 2, test.getSteps().size());
    }
  }

  @Test
  public void suiteLength2AndLength4AndLength6() {
    Length length2 = new Length(2);
    Length length4 = new Length(4);
    Length length6 = new Length(6);
    And and = new And(length2, length4, length6);

    CalculatorModel calculator = new CalculatorModel();
    OSMOTester tester = new OSMOTester(calculator);
    tester.addTestEndCondition(and);
    tester.addSuiteEndCondition(and);
    tester.generate();
    TestSuite testLog = calculator.getHistory();
    List<TestCase> history = testLog.getFinishedTestCases();
    assertEquals("Number of tests generated", 6, history.size());
    for (TestCase test : history) {
      assertEquals("Number of steps in a test case", 6, test.getSteps().size());
    }
  }
}
