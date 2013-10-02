package osmo.tester.scenario;

import org.junit.Before;
import org.junit.Test;
import osmo.common.NullPrintStream;
import osmo.common.OSMOException;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.testmodels.CalculatorModel;

import java.util.List;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class GeneratorTests {
  private OSMOTester tester;
  private OSMOConfiguration config;
  private Scenario scenario;

  @Before
  public void setup() {
    tester = new OSMOTester();
    config = tester.getConfig();
    scenario = new Scenario(false);
    scenario.addStartup("start", "increase", "increase", "increase", "increase");
    config.addModelObject(new CalculatorModel(NullPrintStream.stream));
    config.setScenario(scenario);
    config.setSuiteEndCondition(new Length(5));
  }
  
  @Test
  public void invalidStepsStart() {
    scenario = new Scenario(false);
    scenario.addStartup("hou", "houhou", "increase", "increase", "mo");
    config.setScenario(scenario);
    try {
      tester.generate(444);
      fail("Starting generation with invalid step definitions should fail");
    } catch (OSMOException e) {
      String expected = "Scenario startup script contains steps not found in model:[hou, houhou, mo]";
      String actual = e.getMessage();
      assertEquals("Failure when startup script has invalid step definitions", expected, actual);
    }
  }

  @Test
  public void doubleSlice() {
    scenario.addSlice("increase", 1, 1);
    scenario.addSlice("increase", 2, 2);
    try {
      tester.generate(444);
      fail("Starting generation with invalid step definitions should fail");
    } catch (OSMOException e) {
      String expected = "Duplicate slices not allowed:increase";
      String actual = e.getMessage();
      assertEquals("Failure when startup script has invalid step definitions", expected, actual);
    }
  }

  @Test
  public void invalidStepsSlice() {
    scenario = new Scenario(false);
    scenario.addStartup("start", "increase", "increase", "increase", "increase");
    scenario.addSlice("mama", 0, 1);
    scenario.addSlice("decrease", 0, 2);
    scenario.addSlice("papa", 0, 2);
    config.setScenario(scenario);
    try {
      tester.generate(444);
      fail("Starting generation with invalid step definitions should fail");
    } catch (OSMOException e) {
      String expected = "Scenario slices contains steps not found in model:[mama, papa]";
      String actual = e.getMessage();
      assertEquals("Failure when startup script has invalid step definitions", expected, actual);
    }
  }

  @Test
  public void noStartupLimitedDecrease() {
    scenario = new Scenario(false);
    config.setScenario(scenario);
    config.setTestEndCondition(new Length(20));
    scenario.addSlice("decrease", 0, 2);
    tester.generate(444);
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    for (TestCase test : tests) {
      int count = 0;
      List<TestCaseStep> steps = test.getSteps();
      boolean decrease = false;
      boolean increase = false;
      for (int i = 0 ; i < 20 ; i++) {
        String name = steps.get(i).getName();
        if (name.equals("decrease")) decrease = true;
        if (name.equals("increase")) increase = true;
        if (name.equals("decrease")) count++;
      }
      assertTrue("Decrease step should be found in test", decrease);
      assertTrue("Increase step should be found in test", increase);
      assertEquals("Number of decrease steps", 2, count);
    }
  }

  @Test
  public void startupStrictSlice() {
    scenario = new Scenario(true);
    config.setScenario(scenario);
    config.setTestEndCondition(new Length(20));
    scenario.addSlice("start", 0, 1);
    scenario.addSlice("increase", 2, 0);
    tester.generate(444);
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getAllTestCases();

    for (TestCase test : tests) {
      List<TestCaseStep> steps = test.getSteps();
      boolean decrease = false;
      boolean increase = false;
      for (int i = 0 ; i < 20 ; i++) {
        String name = steps.get(i).getName();
        if (name.equals("decrease")) decrease = true;
        if (name.equals("increase")) increase = true;
      }
      assertFalse("Decrease step should not be found in test", decrease);
      assertTrue("Increase step should be found in test", increase);
    }
  }
  
  @Test
  public void startupSequenceWithLimitedIncreaseSlice() {
    scenario.addSlice("increase", 0, 2);
    config.setTestEndCondition(new Length(13));
    tester.generate(444);
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    assertStartSequences();
    assertSliceAfterStart(tests.get(0), "increase", 2);
    assertSliceAfterStart(tests.get(1), "increase", 2);
    assertSliceAfterStart(tests.get(2), "increase", 2);
    assertSliceAfterStart(tests.get(3), "increase", 2);
    assertSliceAfterStart(tests.get(4), "increase", 2);
  }
  
  @Test
  public void startupSequenceWithLimitedDecreaseSlice() {
    scenario.addSlice("decrease", 0, 2);
    config.setTestEndCondition(new Length(13));
    tester.generate(444);
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    assertStartSequences();
    assertSliceAfterStart(tests.get(0), "decrease", 2);
    assertSliceAfterStart(tests.get(1), "decrease", 2);
    assertSliceAfterStart(tests.get(2), "decrease", 2);
    assertSliceAfterStart(tests.get(3), "decrease", 2);
    assertSliceAfterStart(tests.get(4), "decrease", 2);
  }

  @Test
  public void startupSequenceWithAllSlices() {
    scenario.addSlice("decrease", 3, 0);
    scenario.addSlice("increase", 0, 4);
    tester.getConfig().setFailWhenNoWayForward(false);
    tester.generate(444);
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    assertStartSequences();
    for (TestCase test : tests) {
      int increaseCount = 0;
      int decreaseCount = 0;
      List<TestCaseStep> steps = test.getSteps();
      for (int i = 5 ; i < steps.size() ; i++) {
        String name = steps.get(i).getName();
        if (name.equals("increase")) increaseCount++;
        if (name.equals("decrease")) decreaseCount++;
      }
      assertTrue("Number of increase steps should be <= 4 was "+increaseCount, increaseCount <= 4);
      assertTrue("Number of decrease steps should be >= 3 was " + increaseCount, decreaseCount >= 3);
    }
  }

  @Test
  public void startupSequenceWithRandomEnd() {
    config.setTestEndCondition(new Length(20));
    tester.generate(444);
    assertStartSequences();
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    for (TestCase test : tests) {
      List<TestCaseStep> steps = test.getSteps();
      boolean decrease = false;
      boolean increase = false;
      for (int i = 5 ; i < 20 ; i++) {
        String name = steps.get(i).getName();
        if (name.equals("decrease")) decrease = true;
        if (name.equals("increase")) increase = true;
      }
      assertTrue("Decrease step should be found in test", decrease);
      assertTrue("Increase step should be found in test", increase);
    }
  }

  private void assertStartSequences() {
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    assertStartSequence(tests.get(0));
    assertStartSequence(tests.get(1));
    assertStartSequence(tests.get(2));
    assertStartSequence(tests.get(3));
    assertStartSequence(tests.get(4));
  }
  
  private void assertStartSequence(TestCase test) {
    List<TestCaseStep> steps = test.getSteps();
    assertEquals("Step name at index 0", "start", steps.get(0).getName());
    assertEquals("Step name at index 1", "increase", steps.get(1).getName());
    assertEquals("Step name at index 2", "increase", steps.get(2).getName());
    assertEquals("Step name at index 3", "increase", steps.get(3).getName());
    assertEquals("Step name at index 4", "increase", steps.get(4).getName());
  }
  
  private void assertSliceAfterStart(TestCase test, String step, int expectedCount) {
    int count = 0;
    List<TestCaseStep> steps = test.getSteps();
    boolean decrease = false;
    boolean increase = false;
    for (int i = 5 ; i < 13 ; i++) {
      String name = steps.get(i).getName();
      if (name.equals("decrease")) decrease = true;
      if (name.equals("increase")) increase = true;
      if (name.equals(step)) count++;
    }
    assertTrue("Decrease step should be found in test", decrease);
    assertTrue("Increase step should be found in test", increase);
    assertEquals("Number of steps:"+step, expectedCount, count);
  }
}
