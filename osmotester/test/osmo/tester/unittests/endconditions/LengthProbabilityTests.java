package osmo.tester.unittests.endconditions;

import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.unittests.testmodels.CalculatorModel;

import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class LengthProbabilityTests {
  private void testWith(int suiteExpected, int testExpected, int min, int max, double probability) {
    OSMOTester tester = new OSMOTester();
    tester.setModelFactory(new ReflectiveModelFactory(CalculatorModel.class));
    LengthProbability ec = new LengthProbability(min, max, probability);
    tester.setTestEndCondition(ec);
    tester.setSuiteEndCondition(ec);
    tester.generate(111);
    TestSuite suite = tester.getSuite();
    List<TestCase> history = suite.getAllTestCases();
    assertEquals("Number of tests generated", suiteExpected, history.size());
    TestCase test = history.get(0);
    assertEquals("Number of steps in a test case", testExpected, test.getSteps().size());
  }

  @Test
  public void length1WithProbability() {
    testWith(5, 5, 1, 5, 0.1d);
  }

  @Test
  public void length0() {
  //probability 0 means it runs forever (until max if given)
    testWith(3, 3, 0, 3, 0);
  }

  @Test
  public void minMaxMismatch() {
    try {
      testWith(0, 0, 2, 1, 1);
      fail("Greater min than max should fail.");
    } catch (Exception e) {
      assertEquals("Given minimum length (2) greater than maximum length (1). Must be the other way around.", e.getMessage());
    }
  }

  @Test
  public void negativeMin() {
    try {
      testWith(0, 0, -1, 1, 1);
      fail("Negative min length should throw exception.");
    } catch (Exception e) {
      assertEquals("Length cannot be < 0, was -1.", e.getMessage());
    }
  }

  @Test
  public void negativeMax() {
    try {
      testWith(1, 0, 1, -1, 1);
      fail("Negative max length should throw exception.");
    } catch (Exception e) {
      assertEquals("Maximum length cannot be negative. Given (-1). Use 0 to disable max length.", e.getMessage());
    }
  }

  @Test
  public void negativeProbability() {
    try {
      testWith(0, 0, 1, 1, -1);
      fail("Negative probability should throw exception.");
    } catch (Exception e) {
      assertEquals("Probability threshold must be between 0 and 1. Was -1.0.", e.getMessage());
    }
  }
}
