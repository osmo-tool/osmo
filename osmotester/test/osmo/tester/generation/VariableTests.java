package osmo.tester.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.testmodels.VariableModel1;
import osmo.tester.testmodels.VariableModel2;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class VariableTests {
  private OSMOTester osmo = null;
  private VariableTestListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    osmo.setSeed(123);
    listener = new VariableTestListener();
    osmo.addListener(listener);
  }

  @Test
  public void variableModel1() {
    VariableModel1 model = new VariableModel1();
    osmo.addModelObject(model);
    Length length3 = new Length(3);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length3);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    for (TestCase test : tests) {
      List<TestStep> steps = test.getSteps();
//      int expected = 1;
//      boolean expectedB = false;
//      for (TestStep step : steps) {
//        Map<String, Object> valuesBefore = step.getStateValuesBefore();
//        assertState(valuesBefore, expected, expectedB, "before");
//
//        Map<String, Object> valuesAfter = step.getStateValuesAfter();
//        assertState(valuesAfter, expected + 1, !expectedB, "after");
//        expected++;
//        expectedB = !expectedB;
//      }
    }
  }

  private void assertState(Map<String, Object> values, int expected, boolean expectedB, String point) {
    int i1 = (Integer) values.get("i1");
    float f1 = (Float) values.get("f1");
    double d1 = (Double) values.get("d1");
    boolean b1 = (Boolean) values.get("b1");
    assertEquals("State int value " + point + " stored before transition", expected, i1);
    assertEquals("State float value " + point + " stored before transition", expected + 0.1f, f1);
    assertEquals("State double value " + point + " stored before transition", expected + 0.2d, d1);
    assertEquals("State boolean value " + point + " stored before transition", expectedB, b1);
  }

  @Test
  public void variableModel2SingleTest() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    Length length10 = new Length(10);
    Length length1 = new Length(1);
    osmo.addTestEndCondition(length10);
    osmo.addSuiteEndCondition(length1);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCase test = tests.get(0);
    String expectedSet = "[v1, v2, v3, v2, v3, v1, v3, v3]";
    String expectedRange = "[5, 3, 1, 4, 5, 2, 3, 1]";
    assertModel2Values(test, expectedSet, expectedRange);
  }

  @Test
  public void variableModel2TwoTests() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    Length length9 = new Length(9);
    Length length2 = new Length(2);
    osmo.addTestEndCondition(length9);
    osmo.addSuiteEndCondition(length2);
    osmo.generate();
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCase test = tests.get(0);
    String expectedSet = "[v1, v2, v3, v2, v3, v1, v3]";
    String expectedRange = "[5, 3, 1, 4, 5, 2, 3]";
    assertModel2Values(test, expectedSet, expectedRange);
    test = tests.get(1);
    expectedSet = "[v3, v2, v1, v3, v3, v1, v1]";
    expectedRange = "[1, 3, 5, 1, 1, 5, 2]";
    assertModel2Values(test, expectedSet, expectedRange);
  }

  private void assertModel2Values(TestCase test, String expectedSet, String expectedRange) {
    Map<String, ModelVariable> variables = test.getStepVariables();
    ModelVariable set = variables.get("named-set");
    ModelVariable range = variables.get("range");
    assertNotNull("Set variable should be present", set);
    assertNotNull("Range variable should be present", range);
    assertEquals("Generated values for set", expectedSet, set.getValues().toString());
    assertEquals("Generated values for range", expectedRange, range.getValues().toString());
  }

  @Test
  public void collectionCounter() {
    VariableModel2 model = new VariableModel2();
    osmo.addModelObject(model);
    Length length9 = new Length(9);
    Length length2 = new Length(2);
    osmo.addTestEndCondition(length9);
    osmo.addSuiteEndCondition(length2);
    osmo.generate();
    TestSuite suite = osmo.getSuite();
    List<TestCase> tests = suite.getFinishedTestCases();
    TestCase test = tests.get(0);
    Map<String, ModelVariable> variables = test.getTestVariables();
    ModelVariable valueCount = variables.get("valueCount");
    ModelVariable first = variables.get("first");
    ModelVariable second = variables.get("second");
    assertNotNull("ValueCount variable should be present", valueCount);
    assertNotNull("First variable should be present", first);
    assertNotNull("Second variable should be present", second);
    assertEquals("Generated values for ValueCount", "[0, 1, 2, 3, 4, 5, 6, 7]", valueCount.getValues().toString());
    assertEquals("Generated values for first", "[true]", first.getValues().toString());
    assertEquals("Generated values for second", "[false, true]", second.getValues().toString());

  }
}
