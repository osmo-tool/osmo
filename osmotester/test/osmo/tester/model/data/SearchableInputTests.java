package osmo.tester.model.data;

import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class SearchableInputTests {
  private TestSuite suite = null;

  @Before
  public void reset() {
    OSMOConfiguration.reset();
    OSMOConfiguration.setSeed(1);
    suite = new TestSuite();
    TestCase test = suite.startTest();
    test.addStep(new FSMTransition("test"));
  }

  @Test
  public void valueRange() {
    ValueRange<Integer> range = new ValueRange<>(1, 10);
    range.setSuite(suite);
    range.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    String name = "test-range";
    range.setName(name);
    Collection<Integer> expected = new ArrayList<>();
    for (int i = 0 ; i < 11 ; i++) {
      range.next();
      expected.add(i % 10 + 1);
    }
    Map<String, ModelVariable> variables = suite.getStepVariables();
    ModelVariable variable = variables.get("test-range");
    Collection actual = variable.getValues();
    assertEquals("Observed generated values", expected, actual);
  }

  @Test
  public void valueSet() {
    ValueSet<String> set = new ValueSet<>("v1", "v2", "v3");
    set.setSuite(suite);
    set.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    String name = "test-set";
    set.setName(name);
    Collection<String> expected = new ArrayList<>();
    Collection<String> expected2 = new LinkedHashSet<>();
    for (int i = 0 ; i < 5 ; i++) {
      set.next();
      String value = "v" + (i % 3 + 1);
      expected.add(value);
      expected2.add(value);
    }
    TestCase test = suite.getAllTestCases().get(0);
    Map<String, ModelVariable> stepVariables = test.getStepVariables();
    ModelVariable variable = stepVariables.get("test-set");
    Collection actual = variable.getValues();
    assertEquals("Observed generated values", expected, actual);

    Map<String, ModelVariable> suiteVariables = suite.getTestVariables();
    ModelVariable variable2 = suiteVariables.get("test-set");
    Collection actual2 = variable2.getValues();
    //the collection types are different, thus the toString()
    assertEquals("Observed generated values", expected2.toString(), actual2.toString());
  }
}
