package osmo.tester.unittests.generation;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.unittests.testmodels.VariableModel2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class VariableTests {
  private OSMOTester osmo = null;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
  }

  @Test
  public void variableModel2SingleTest() {
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    VariableModel2 model = new VariableModel2();
    factory.add(model);
    osmo.setModelFactory(factory);
    Length length10 = new Length(10);
    Length length1 = new Length(1);
    osmo.setTestEndCondition(length10);
    osmo.setSuiteEndCondition(length1);
    osmo.generate(123);
    TestSuite suite = model.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    TestCase test = tests.get(0);
    String expectedSet = "[v1, v2, v3]";
    String expectedRange = "[5, 3, 1, 4, 2]";
    String expectedHello = "[MyTV 1, MyTV 2, MyTV 3, MyTV 4, MyTV 5, MyTV 6, MyTV 7, MyTV 8, MyTV 9, MyTV 10]";
    assertModel2Values(test, expectedSet, expectedRange, expectedHello);
  }

  @Test
  public void variableModel2TwoTests() {
    osmo.addModelObject(new VariableModel2());
    Length length9 = new Length(9);
    Length length2 = new Length(2);
    osmo.setTestEndCondition(length9);
    osmo.setSuiteEndCondition(length2);
    osmo.generate(123);
    TestSuite suite = osmo.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    TestCase test = tests.get(0);
    String expectedSet = "[v1, v2, v3]";
    String expectedRange = "[5, 3, 1, 4, 2]";
    String expectedHello = "[MyTV 1, MyTV 2, MyTV 3, MyTV 4, MyTV 5, MyTV 6, MyTV 7, MyTV 8, MyTV 9]";
    assertModel2Values(test, expectedSet, expectedRange, expectedHello);
    test = tests.get(1);
    expectedSet = "[v3, v2, v1]";
    expectedRange = "[1, 3, 5, 2]";
    expectedHello = "[MyTV 10, MyTV 11, MyTV 12, MyTV 13, MyTV 14, MyTV 15, MyTV 16, MyTV 17, MyTV 18]";
    assertModel2Values(test, expectedSet, expectedRange, expectedHello);
  }

  private void assertModel2Values(TestCase test, String expectedSet, String expectedRange, String expectedHello) {
    Map<String,Collection<String>> variableValues = test.getCoverage().getVariableValues();
    Collection<String> set = variableValues.get("named-set");
    Collection<String> range = variableValues.get("range");
    Collection<String> hello = variableValues.get("Hello There");
    assertNotNull("Set variable should be present", set);
    assertNotNull("Range variable should be present", range);
    assertNotNull("Hello variable should be present", range);
    assertEquals("Generated values for set", expectedSet, set.toString());
    assertEquals("Generated values for range", expectedRange, range.toString());
    assertEquals("Generated values for hello", expectedHello, hello.toString());
  }

  @Test
  public void collectionCounter() {
    osmo.setModelFactory(new ReflectiveModelFactory(VariableModel2.class));
    Length length9 = new Length(9);
    Length length2 = new Length(2);
    osmo.setTestEndCondition(length9);
    osmo.setSuiteEndCondition(length2);
    osmo.generate(123);
    TestSuite suite = osmo.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    TestCase test = tests.get(0);
    Map<String, Collection<String>> variableValues = test.getCoverage().getVariableValues();
    Collection<String> valueCount = variableValues.get("valueCount");
    Collection<String> first = variableValues.get("first");
    Collection<String> second = variableValues.get("second");
    assertNotNull("ValueCount variable should be present", valueCount);
    assertNotNull("First variable should be present", first);
    assertNotNull("Second variable should be present", second);
    assertEquals("Generated values for ValueCount", "[0, 1, 2, 3, 4, 5, 6, 7]", valueCount.toString());
    assertEquals("Generated values for first", "[true]", first.toString());
    assertEquals("Generated values for second", "[false, true]", second.toString());
  }
}
