package osmo.tester.dsm;

import org.junit.Before;
import org.junit.Test;
import osmo.common.log.Logger;
import osmo.tester.generator.endcondition.data.DataCoverageRequirement;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class AsciiParserTests {
  private AsciiParser parser;

  @Before
  public void setUp() throws Exception {
    parser = new AsciiParser();
  }

  //TODO: test step names with space etc
  @Test
  public void oneStep() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob, 5";
    DSMConfiguration reqs = parser.parse(input);
    List<String> steps = reqs.getStepRequirements();
    assertEquals("Number of step requirements", 5, steps.size());
    assertStepNameAndTimes(steps, "bob", 5);
  }

  @Test
  public void twoSteps() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob, 5\n" +
            "jones, 6";
    DSMConfiguration reqs = parser.parse(input);
    List<String> steps = reqs.getStepRequirements();
    assertEquals("Number of step requirements", 11, steps.size());
    assertStepNameAndTimes(steps, "bob", 5);
    assertStepNameAndTimes(steps, "jones", 6);
  }

  @Test
  public void stepWithSpace() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob jones, 5\n" +
            "jones bobe, 6";
    DSMConfiguration reqs = parser.parse(input);
    List<String> steps = reqs.getStepRequirements();
    assertEquals("Number of step requirements", 11, steps.size());
    assertStepNameAndTimes(steps, "bob jones", 5);
    assertStepNameAndTimes(steps, "jones bobe", 6);
  }

  @Test
  public void noRequirementsError() {
    String input = "setting, value\n" +
            "model factory, hello\n";
    String expected = "Input does not define any valid coverage requirements (steps or variables).";
    assertParseError(input, expected);
  }

  @Test
  public void invalidPairCount() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob jones, 5\n" +
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
  public void stepCountNegative() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob jones, 5\n" +
            "jones bobe, -2";
    String expected = "Number of times the steps is required needs to be > 0. Was -2";
    assertParseError(input, expected);
  }

  @Test
  public void stepCountZero() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "step, times\n" +
            "bob jones, 0\n" +
            "jones bobe, 3";
    String expected = "Number of times the steps is required needs to be > 0. Was 0";
    assertParseError(input, expected);
  }

  @Test
  public void oneVariableNoSteps() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "variable, values\n" +
            "v1, 0\n";
    DSMConfiguration reqs = parser.parse(input);
    List<DataCoverageRequirement> data = reqs.getDataRequirements();
    assertEquals("Number of data requirements", 1, data.size());
    assertDataNameAndValues(data.get(0), "v1", 0);
  }

  @Test
  public void oneVariableTwoValues() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "variable, values\n" +
            "v1, 0\n" +
            "v1, 3\n";
    DSMConfiguration reqs = parser.parse(input);
    List<DataCoverageRequirement> data = reqs.getDataRequirements();
    assertEquals("Number of data requirements", 1, data.size());
    assertDataNameAndValues(data.get(0), "v1", 0, 3);
  }

  @Test
  public void twoVariables() {
    String input = "setting, value\n" +
            "model factory, hello\n\n" +
            "variable, values\n" +
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
  public void settingsOneModelObject() {
    String input = "setting, value\n" +
            "algorithm, hello\n" +
            "model factory, bob\n\n" +
            "variable, values\n" +
            "v1, 1\n";
    //TODO: test different caps
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
            "variable, values\n" +
            "v1, 1\n";
    assertParseError(input, "Only one model factory allowed.");
  }

  @Test
  public void settingsTwoAlgorithms() {
    String input = "setting, value\n" +
            "algorithm, hello\n" +
            "algorithm, bob\n" +
            "variable, values\n" +
            "v1, 1\n";
    assertParseError(input, "Only one algorithm allowed.");
  }

  @Test
  public void settingsNoModelObject() {
    String input = "setting, value\n" +
            "algorithm, hello\n" +
            "variable, values\n" +
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

  private void assertParseError(String input, String expectedMsg) {
    try {
      parser.parse(input);
      fail("Invalid data parsing should produce error");
    } catch (IllegalArgumentException e) {
      assertEquals("Error for invalid parser input", expectedMsg, e.getMessage());
    }
  }

  private void assertStepNameAndTimes(Collection<String> req, String step, int times) {
    int actual = 0;
    for (String name : req) {
      if (name.equals(step)) {
        actual++;
      }
    }
    assertEquals("Required times", times, actual);
  }
}
