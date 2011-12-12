package osmo.tester.scripting.dsm;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;
import osmo.tester.model.dataflow.ScriptedValueProvider;
import osmo.tester.model.dataflow.ValueSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class AsciiParserTests {
  private AsciiParser parser;

  @Before
  public void setUp() throws Exception {
    parser = new AsciiParser();
  }

  @Test
  public void minOneStep() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob, >=5";
    DSMConfiguration reqs = parser.parse(input);
    Collection<StepRequirement> stepReqs = reqs.getStepRequirements();
    assertEquals("Number of step requirements", 1, stepReqs.size());
    assertStepNameAndMin(stepReqs, "bob", 5);
  }

  @Test
  public void maxOneStep() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob, <=5";
    DSMConfiguration reqs = parser.parse(input);
    Collection<StepRequirement> stepReqs = reqs.getStepRequirements();
    assertEquals("Number of step requirements", 1, stepReqs.size());
    assertStepNameAndMax(stepReqs, "bob", 5);
  }

  @Test
  public void exactlyOneStep() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob, ==5";
    DSMConfiguration reqs = parser.parse(input);
    Collection<StepRequirement> stepReqs = reqs.getStepRequirements();
    assertEquals("Number of step requirements", 1, stepReqs.size());
    assertStepNameAndExact(stepReqs, "bob", 5);
  }

  @Test
  public void minTwoSteps() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob, >=5\n" +
            "jones, >=6";
    DSMConfiguration reqs = parser.parse(input);
    Collection<StepRequirement> stepReqs = reqs.getStepRequirements();
    assertEquals("Number of step requirements", 2, stepReqs.size());
    assertStepNameAndMin(stepReqs, "bob", 5);
    assertStepNameAndMin(stepReqs, "jones", 6);
  }

  @Test
  public void maxTwoSteps() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob, <=5\n" +
            "jones, <=6";
    DSMConfiguration reqs = parser.parse(input);
    Collection<StepRequirement> stepReqs = reqs.getStepRequirements();
    assertEquals("Number of step requirements", 2, stepReqs.size());
    assertStepNameAndMax(stepReqs, "bob", 5);
    assertStepNameAndMax(stepReqs, "jones", 6);
  }

  @Test
  public void exactTwoSteps() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob, ==5\n" +
            "jones, >=6";
    DSMConfiguration reqs = parser.parse(input);
    Collection<StepRequirement> stepReqs = reqs.getStepRequirements();
    assertEquals("Number of step requirements", 2, stepReqs.size());
    assertStepNameAndExact(stepReqs, "bob", 5);
    assertStepNameAndMin(stepReqs, "jones", 6);
  }

  @Test
  public void exactZeroSteps() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob, == 0\n" +
            "jones, <= 6";
    DSMConfiguration reqs = parser.parse(input);
    Collection<StepRequirement> stepReqs = reqs.getStepRequirements();
    assertEquals("Number of step requirements", 2, stepReqs.size());
    assertStepNameAndExact(stepReqs, "bob", 0);
    assertStepNameAndMax(stepReqs, "jones", 6);
  }

  @Test
  public void stepWithSpace() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob jones, >=5\n" +
            "jones bobe, >=6";
    DSMConfiguration reqs = parser.parse(input);
    Collection<StepRequirement> stepReqs = reqs.getStepRequirements();
    assertEquals("Number of step requirements", 2, stepReqs.size());
    assertStepNameAndMin(stepReqs, "bob jones", 5);
    assertStepNameAndMin(stepReqs, "jones bobe", 6);
  }

  @Test
  public void noRequirementsError() {
    String input = "setting, value\n" +
            "model factory, hello\n";
    String expected = "Input does not define any valid coverage requirements (steps or variables) or script.";
    assertParseError(input, expected);
  }

  @Test
  public void invalidPairCount() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob jones, >=5\n" +
            "jones bobe";
    String expected = "Table rows must have 2 cells. \"step, times\" had a row with 1 cell(s).";
    assertParseError(input, expected);
  }

  @Test
  public void noSteps() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n";
    String expected = "Table \"step, times\" has no content.";
    assertParseError(input, expected);
  }

  @Test
  public void stepMinCountNegative() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob jones, >=5\n" +
            "jones bobe, >=-2";
    String expected = "Number of times the steps is required needs to be > 0. Was -2.";
    assertParseError(input, expected);
  }

  @Test
  public void stepMaxCountNegative() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob jones, >=5\n" +
            "jones bobe, <=-2";
    String expected = "Number of times the steps is required needs to be > 0. Was -2.";
    assertParseError(input, expected);
  }

  @Test
  public void stepExactCountNegative() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob jones, >=5\n" +
            "jones bobe, ==-2";
    String expected = "Number of times the steps is required needs to be > 0. Was -2.";
    assertParseError(input, expected);
  }

  @Test
  public void stepMinCountZero() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob jones, >= 0\n" +
            "jones bobe, >= 3";
    String expected = "Number of times the steps is required needs to be > 0. Was 0.";
    assertParseError(input, expected);
  }

  @Test
  public void stepMaxCountZero() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob jones, <= 0\n" +
            "jones bobe, >= 3";
    String expected = "Number of times the steps is required needs to be > 0. Was 0.";
    assertParseError(input, expected);
  }

  @Test
  public void oneVariableCoverageNoSteps() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "variable, coverage\n" +
            "v1,  0\n";
    DSMConfiguration reqs = parser.parse(input);
    List<DataCoverageRequirement> data = reqs.getDataRequirements();
    assertEquals("Number of data requirements", 1, data.size());
    assertDataNameAndValues(data.get(0), "v1", 0);
  }

  @Test
  public void oneVariableTwoValuesCoverage() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "variable, coverage\n" +
            "v1, 0\n" +
            "v1, 3\n";
    DSMConfiguration reqs = parser.parse(input);
    List<DataCoverageRequirement> data = reqs.getDataRequirements();
    assertEquals("Number of data requirements", 1, data.size());
    assertDataNameAndValues(data.get(0), "v1", 0, 3);
  }

  @Test
  public void twoVariablesCoverage() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "variable, coverage\n" +
            "v1, 1\n" +
            "v1, 2\n" +
            "v2, 3\n" +
            "v1, 4\n";
    DSMConfiguration reqs = parser.parse(input);
    List<DataCoverageRequirement> data = reqs.getDataRequirements();
    assertEquals("Number of data requirements", 2, data.size());
    assertDataNameAndValues(data.get(0), "v1", 1, 2, 4);
    assertDataNameAndValues(data.get(1), "v2", 3);
  }

  @Test
  public void twoScriptedVariables() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "variable, values\n" +
            "v1, 1\n" +
            "v1, 2\n" +
            "v2, 3\n" +
            "v1, 4\n";
    DSMConfiguration reqs = parser.parse(input);
    ScriptedValueProvider scripter = reqs.getScriptedValueProvider();
    Map<String, ValueSet<String>> scripts = scripter.getScripts();
    assertEquals("Number of data requirements", 2, scripts.size());
    assertDataNameAndValues(scripts, "v1", 1, 2, 4);
    assertDataNameAndValues(scripts, "v2", 3);
  }

  @Test
  public void settingsOneModelObject() {
    String input = "setting, value\n" +
            "algorithm, hello\n" +
            "model factory, bob\n\n" +
            "variable, coverage\n" +
            "v1, 1\n";
    DSMConfiguration reqs = parser.parse(input);
    assertEquals("Configured algorithm", "hello", reqs.getAlgorithm());
    assertEquals("Model factory", "bob", reqs.getModelFactory());
    List<DataCoverageRequirement> data = reqs.getDataRequirements();
    assertEquals("Number of data requirements", 1, data.size());
    assertDataNameAndValues(data.get(0), "v1", 1);
  }

  @Test
  public void settingsTwoModelObject() {
    String input = "setting, value\n" +
            "algorithm, hello\n" +
            "model factory, bob\n" +
            "model factory, jones\n\n" +
            "variable, coverage\n" +
            "v1, 1\n";
    assertParseError(input, "Only one model factory allowed.");
  }

  @Test
  public void settingsTwoAlgorithms() {
    String input = "setting, value\n" +
            "algorithm, hello\n" +
            "algorithm, bob\n" +
            "variable, coverage\n" +
            "v1, 1\n";
    assertParseError(input, "Only one algorithm allowed.");
  }

  @Test
  public void settingsNoModelObject() {
    String input = "setting, value\n" +
            "algorithm, hello\n" +
            "variable, coverage\n" +
            "v1, 1\n";
    assertParseError(input, "Input does not define model object factory.");
  }

  private void assertDataNameAndValues(DataCoverageRequirement req, String name, Object... values) {
    assertEquals("Variable name", name, req.getName());
    for (Object value : values) {
      Collection<String> actual = req.getValues();
      assertTrue("Variable " + name + " does not have required value:" + value, actual.contains("" + value));
    }
  }

  private void assertDataNameAndValues(Map<String, ValueSet<String>> scripts, String name, Object... values) {
    ValueSet<String> script = scripts.get(name);
    assertNotNull("Variable should be scripted:" + name, script);
    for (Object value : values) {
      assertEquals("Variable " + name + " does not have required value in order:" + value + " total:" + script.getOptions(), "" + value, script.next());
    }
  }

  private void assertParseError(String input, String expectedMsg) {
    try {
      parser.parse(input);
      fail("Invalid data parsing should produce error");
    } catch (IllegalArgumentException e) {
      assertEquals("Error for invalid parser input", expectedMsg, e.getMessage());
    }
  }

  private void assertStepNameAndMin(Collection<StepRequirement> reqs, String step, int times) {
    for (StepRequirement req : reqs) {
      if (req.getStep().equals(step)) {
        Integer min = req.getMin();
        assertNotNull("Step requirement should have minimum value defined (" + step + ")", min);
        assertNull("Step requirement should not have maximum value defined (" + step + ")", req.getMax());
        assertEquals("Minimum number of times step '" + step + "' required", times, min.intValue());
        return;
      }
    }
    fail("Required step requirement not found:" + step);
  }

  private void assertStepNameAndMax(Collection<StepRequirement> reqs, String step, int times) {
    for (StepRequirement req : reqs) {
      if (req.getStep().equals(step)) {
        Integer max = req.getMax();
        assertNotNull("Step requirement should have maximum value defined (" + step + ")", max);
        assertNull("Step requirement should not have minimum value defined (" + step + ")", req.getMin());
        assertEquals("Maximum number of times step '" + step + "' required", times, max.intValue());
        return;
      }
    }
    fail("Required step requirement not found:" + step);
  }

  private void assertStepNameAndExact(Collection<StepRequirement> reqs, String step, int times) {
    for (StepRequirement req : reqs) {
      if (req.getStep().equals(step)) {
        Integer max = req.getMax();
        Integer min = req.getMin();
        assertNotNull("Step requirement should have minimum value defined (" + step + ")", min);
        assertNotNull("Step requirement should have maximum value defined (" + step + ")", max);
        assertEquals("Minimum number of times step '" + step + "' required", times, min.intValue());
        assertEquals("Maximum number of times step '" + step + "' required", times, max.intValue());
        return;
      }
    }
    fail("Required step requirement not found:" + step);
  }
}
