package osmo.tester.unittests.endconditions;

import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.unittests.testmodels.CalculatorModel;

import java.util.List;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;

/** @author Teemu Kanstren */
public class LengthTests {
  @Test
  public void length10() {
    testWithLength(10);
  }

  private void testWithLength(int expectedLength) {
    testWithLength(expectedLength, expectedLength);
  }
  
  private void testWithLength(int goal, int expectedLength) {
    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    Length length = new Length(goal);
    tester.setTestEndCondition(length);
    tester.setSuiteEndCondition(length);
    tester.generate(111);
    TestSuite suite = tester.getSuite();
    List<TestCase> history = suite.getFinishedTestCases();
    assertEquals("Number of tests generated", expectedLength, history.size());
    for (TestCase test : history) {
      assertEquals("Number of steps in a test case", expectedLength, test.getSteps().size());
    }
  }

  @Test
  public void length1() {
    testWithLength(1);
  }

  @Test
  public void length0() {
    testWithLength(0, 1);
  }

  @Test
  public void negativeLength() {
    try {
      testWithLength(-1);
      fail("Negative length should throw exception.");
    } catch (Exception e) {
      assertEquals("Length cannot be < 0, was -1.", e.getMessage());
    }
  }
  
  @Test
  public void suiteLengthTestsNotKept() {
    OSMOTester tester = new OSMOTester();
    tester.getConfig().setKeepTests(false);
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    Length length = new Length(10);
    tester.setTestEndCondition(length);
    tester.setSuiteEndCondition(length);
    tester.generate(111);
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    assertEquals("Number of tests generated", 10, suite.currentTestNumber());
    assertEquals("Number of tests kept", 0, tests.size());
  }
  
  @Test
  public void length1WithZeroRequested() {
    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    Length length = new Length(0);
    tester.setTestEndCondition(length);
    tester.setSuiteEndCondition(length);
    tester.generate(111);
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    assertEquals("Number of tests generated", 1, tests.size());
    assertEquals("Number of steps", 1, tests.get(0).getAllStepNames().size());
  }
}
