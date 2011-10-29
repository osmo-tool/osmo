package osmo.tester.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.testmodels.VariableModel1;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * @author Teemu Kanstren
 */
public class VariableTests {
  private OSMOTester osmo = null;
  private VariableTestListener listener;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
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
      int expected = 1;
      boolean expectedB = false;
      for (TestStep step : steps) {
        Map<String,Object> valuesBefore = step.getStateValuesBefore();
        assertState(valuesBefore, expected, expectedB, "before");

        Map<String,Object> valuesAfter = step.getStateValuesAfter();
        assertState(valuesAfter, expected+1, !expectedB, "after");
        expected++;
        expectedB = !expectedB;
      }
    }
  }

  private void assertState(Map<String,Object> values, int expected, boolean expectedB, String point) {
    int i1 = (Integer)values.get("i1");
    float f1 = (Float)values.get("f1");
    double d1 = (Double)values.get("d1");
    boolean b1 = (Boolean)values.get("b1");
    assertEquals("State int value "+point+" stored before transition", expected, i1);
    assertEquals("State float value "+point+" stored before transition", expected+0.1f, f1);
    assertEquals("State double value "+point+" stored before transition", expected+0.2d, d1);
    assertEquals("State boolean value "+point+" stored before transition", expectedB, b1);
  }

  @Test
  public void customObject() {
  }
}
