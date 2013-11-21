package osmo.tester.generation;

import org.junit.Test;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** @author Teemu Kanstren */
public class HistoryTests {
  @Test
  public void historyContainsByName() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("bob"));
    suite.addStep(new FSMTransition("alice"));
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("bob2"));
    suite.addStep(new FSMTransition("alice2"));
    suite.endTest();
    suite.startTest();
    suite.addStep(new FSMTransition("bob3"));
    suite.addStep(new FSMTransition("alice3"));
    suite.endTest();
    assertTrue(suite.contains(new FSMTransition("bob")));
    assertTrue(suite.contains(new FSMTransition("bob2")));
    assertTrue(suite.contains(new FSMTransition("bob3")));
    assertFalse(suite.contains(new FSMTransition("bob4")));
  }

  @Test
  public void currentContainsByName() {
    TestSuite suite = new TestSuite();
    suite.startTest();
    suite.addStep(new FSMTransition("bob"));
    suite.addStep(new FSMTransition("alice"));
    assertTrue(suite.contains(new FSMTransition("bob")));
    assertFalse(suite.contains(new FSMTransition("bob4")));
  }

  @Test
  public void customAttributes() {
    TestCase test = new TestCase();
    test.setAttribute("script", "whooppee");
    assertEquals("Stored attribute in test case", "whooppee", test.getAttribute("script"));
  }

  @Test
  public void variablesNoMerging() {
    TestCase test = new TestCase();
    test.addStep(new FSMTransition("bob"));
//    test.addVariableValue("v1", "hello", false);
//    test.addVariableValue("v1", "world");
//    test.addVariableValue("v1", "world", false);
//
//    Map<String, ModelVariable> variables = test.getStepVariables();
//    assertEquals("Number of variables", 1, variables.size());
//    assertEquals("Number of values", 3, variables.get("v1").getValues().size());
//
//    test.addVariableValue("v2", "added", false);
//    variables = test.getStepVariables();
//    assertEquals("Number of variables", 2, variables.size());
//    assertEquals("Number of values", 3, variables.get("v1").getValues().size());
//    assertEquals("Number of values", 1, variables.get("v2").getValues().size());
  }

  @Test
  public void variablesMerging() {
    TestCase test = new TestCase();
    test.addStep(new FSMTransition("heippa"));
//    test.addVariableValue("v1", "hello", true);
//    test.addVariableValue("v1", "world", true);
//    test.addVariableValue("v1", "world", true);
//
//    Map<String, ModelVariable> variables = test.getTestVariables();
//    assertEquals("Number of variables", 1, variables.size());
//    Collection<Object> v1 = variables.get("v1").getValues();
//    assertEquals("Number of values", 2, v1.size());
//    Iterator<Object> i = v1.iterator();
//    assertEquals("Variable v1 value 1", "hello", i.next());
//    assertEquals("Variable v1 value 2", "world", i.next());
  }
}
