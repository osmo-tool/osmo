package osmo.tester.scripting.manual;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class AsciiParserTests {
  private AsciiParser parser;

  @Before
  public void setUp() throws Exception {
    //Logger.debug = true;
    parser = new AsciiParser();
  }

  @Test
  public void oneStep() {
    String input = "action, name, value\n" +
            "new test,,\n" +
            "step, foo,\n";
    List<TestScript> scripts = parser.parse(input);
    assertEquals("Number of tests", 1, scripts.size());
    TestScript script = scripts.get(0);
    ScriptStep next = script.getSteps().get(0);
    assertEquals("Step name", "foo", next.getTransition());
    assertEquals("Script should have only one step", 1, script.getSteps().size());
  }

  @Test
  public void oneStepOneVariable() {
    String input = "action, name, value\n" +
            "new test,,\n" +
            "step, foo,\n" +
            "variable, bob, who?\n";
    List<TestScript> scripts = parser.parse(input);
    assertEquals("Number of tests", 1, scripts.size());
    TestScript script = scripts.get(0);
    assertStep(script, 0, "foo", "bob", "who?");
  }

  @Test
  public void twoStepsOneVariable() {
    String input = "action, name, value\n" +
            "new test,,\n" +
            "step, foo,\n" +
            "variable, bob1, who1?\n" +
            "step, foo,\n" +
            "variable, bob2, who2?\n";
    List<TestScript> scripts = parser.parse(input);
    assertEquals("Number of tests", 1, scripts.size());
    TestScript script = scripts.get(0);

    assertStep(script, 0, "foo", "bob1", "who1?");
    assertStep(script, 1, "foo", "bob2", "who2?");
  }

  @Test
  public void twoTestsTwoVariables() {
    String input = "action, name, value\n" +
            "new test,,\n" +
            "step, foo,\n" +
            "variable, bob1, who1?\n" +
            "variable, bob1, whoz1?\n" +
            "step, foo,\n" +
            "variable, bob2, who2?\n" +
            "new test,,\n" +
            "step, alice,\n" +
            "variable, bob1, wonderland\n" +
            "variable, bob2, eggbasket\n" +
            "step, foo,\n" +
            "variable, bob2, basketcase\n" +
            "variable, bob2, case-in\n";
    List<TestScript> scripts = parser.parse(input);
    assertEquals("Number of tests", 2, scripts.size());

    TestScript script = scripts.get(0);
    assertEquals("Step count", 2, script.getSteps().size());
    assertStep(script, 0, "foo", "bob1", "who1?", "bob1", "whoz1?");
    assertStep(script, 1, "foo", "bob2", "who2?");

    script = scripts.get(1);
    assertEquals("Step count", 2, script.getSteps().size());
    assertStep(script, 0, "alice", "bob1", "wonderland", "bob2", "eggbasket");
    assertStep(script, 1, "foo", "bob2", "basketcase", "bob2", "case-in");
  }

  private void assertStep(TestScript script, int index, String stepName, String... variables) {
    List<ScriptStep> steps = script.getSteps();
    ScriptStep step = steps.get(index);
    assertEquals("Step name", stepName, step.getTransition());
    List<ScriptValue> values = step.getValues();
    assertEquals("Number of variables in step", variables.length / 2, values.size());
    for (int i = 0 ; i < variables.length ; i += 2) {
      assertVariable(variables[i], variables[i + 1], values.get(i / 2));
    }
  }

  private void assertVariable(String expectedName, String expectedValue, ScriptValue actual) {
    assertEquals("Variable name", expectedName, actual.getVariable());
    assertEquals("Variable value", expectedValue, actual.getValue());
  }
}
