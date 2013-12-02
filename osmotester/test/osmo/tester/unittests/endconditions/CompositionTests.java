package osmo.tester.unittests.endconditions;

import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.logical.And;
import osmo.tester.generator.endcondition.logical.Or;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.unittests.testmodels.CalculatorModel;

import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class CompositionTests {
  @Test
  public void length2AndLength4() {
    Length length2 = new Length(2);
    Length length4 = new Length(4);
    And and = new And(length2, length4);

    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    tester.setTestEndCondition(and);
    tester.setSuiteEndCondition(length2);
    tester.generate(333);
    TestSuite suite = tester.getSuite();
    List<TestCase> history = suite.getFinishedTestCases();
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

    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    tester.setTestEndCondition(and);
    tester.setSuiteEndCondition(length2);
    tester.generate(333);
    TestSuite suite = tester.getSuite();
    List<TestCase> history = suite.getFinishedTestCases();
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

    OSMOTester tester = new OSMOTester();
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    factory.add(new CalculatorModel());
    tester.setModelFactory(factory);
    tester.setTestEndCondition(or);
    tester.setSuiteEndCondition(length2);
    tester.generate(333);
    TestSuite suite = tester.getSuite();
    List<TestCase> history = suite.getFinishedTestCases();
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

    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    tester.setTestEndCondition(and);
    tester.setSuiteEndCondition(and);
    tester.generate(333);
    TestSuite suite = tester.getSuite();
    List<TestCase> history = suite.getFinishedTestCases();
    assertEquals("Number of tests generated", 6, history.size());
    for (TestCase test : history) {
      assertEquals("Number of steps in a test case", 6, test.getSteps().size());
    }
  }
}
