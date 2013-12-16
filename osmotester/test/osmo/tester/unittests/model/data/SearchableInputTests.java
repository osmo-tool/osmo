package osmo.tester.unittests.model.data;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueSet;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class SearchableInputTests {
  private TestSuite suite = null;

  @Before
  public void reset() {
    suite = new TestSuite();
    TestCase test = suite.startTest(1);
    test.addStep(new FSMTransition("test"));
  }

  @Test
  public void valueRange() {
    ValueRange<Integer> range = new ValueRange<>(1, 10);
    range.setSuite(suite);
    String name = "test-range";
    range.setName(name);
    range.setStored(true);
    range.setSeed(111);
    Collection<String> expected = new LinkedHashSet<>();
    for (int i = 0 ; i < 11 ; i++) {
      range.loop();
      int value = i % 10 + 1;
      expected.add("" + value);
    }
    TestCoverage coverage = suite.getCurrentTest().getCoverage();
    Map<String, Collection<String>> variables = coverage.getVariableValues();
    Collection<String> actual = variables.get("test-range");
    assertEquals("Observed generated values", expected, actual);
  }

  @Test
  public void valueSet() {
    ValueSet<String> set = new ValueSet<>("v1", "v2", "v3");
    set.setSuite(suite);
    String name = "test-set";
    set.setName(name);
    set.setStored(true);
    set.setSeed(111);
    Collection<String> expected = new LinkedHashSet<>();
    Collection<String> expected2 = new LinkedHashSet<>();
    for (int i = 0 ; i < 5 ; i++) {
      set.loop();
      String value = "v" + (i % 3 + 1);
      expected.add(value);
      expected2.add(value);
    }
    suite.endTest();
    TestCoverage coverage = suite.getAllTestCases().get(0).getCoverage();
    Map<String, Collection<String>> variables = coverage.getVariableValues();
    Collection<String> actual = variables.get("test-set");
    assertEquals("Observed generated values", expected, actual);

    coverage = suite.getCoverage();
    variables = coverage.getVariableValues();
    Collection<String> actual2 = variables.get("test-set");
    //the collection types are different, thus the toString()
    assertEquals("Observed generated values", expected2.toString(), actual2.toString());
  }
}
