package osmo.tester.unit;

import org.junit.Test;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/** @author Teemu Kanstren */
public class TestModelTests {
  @Test
  public void customAttributes() {
    TestCase test = new TestCase();
    test.setAttribute("script", "whooppee");
    assertEquals("Stored attribute in test case", "whooppee", test.getAttribute("script"));
  }

  @Test
  public void variablesNoMerging() {
    TestCase test = new TestCase();
    test.addVariableValue("v1", "hello", false);
    test.addVariableValue("v1", "world");
    test.addVariableValue("v1", "world", false);

    Map<String, ModelVariable> variables = test.getVariables();
    assertEquals("Number of variables", 1, variables.size());
    assertEquals("Number of values", 3, variables.get("v1").getValues().size());

    test.addVariableValue("v2", "added", false);
    assertEquals("Number of variables", 2, variables.size());
    assertEquals("Number of values", 3, variables.get("v1").getValues().size());
    assertEquals("Number of values", 1, variables.get("v2").getValues().size());
  }

  @Test
  public void variablesMerging() {
    TestCase test = new TestCase();
    test.addVariableValue("v1", "hello", true);
    test.addVariableValue("v1", "world", true);
    test.addVariableValue("v1", "world", true);

    Map<String, ModelVariable> variables = test.getVariables();
    assertEquals("Number of variables", 1, variables.size());
    Collection<Object> v1 = variables.get("v1").getValues();
    assertEquals("Number of values", 2, v1.size());
    Iterator<Object> i = v1.iterator();
    assertEquals("Variable v1 value 1", "hello", i.next());
    assertEquals("Variable v1 value 2", "world", i.next());
  }
}
