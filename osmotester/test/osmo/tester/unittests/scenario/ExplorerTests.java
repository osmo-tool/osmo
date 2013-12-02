package osmo.tester.unittests.scenario;

import org.junit.Before;
import org.junit.Test;
import osmo.common.OSMOException;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.explorer.OSMOExplorer;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.scenario.Scenario;
import osmo.tester.unittests.explorer.testmodels.CounterFactory;
import osmo.tester.unittests.explorer.testmodels.CounterModel;

import java.util.List;

import static org.junit.Assert.*;

/** @author Teemu Kanstren */
public class ExplorerTests {
  private OSMOExplorer explorer;
  private ExplorationConfiguration config;
  private Scenario scenario;
  
  @Before
  public void setup() {
    CounterFactory factory = new CounterFactory();
    config = new ExplorationConfiguration(factory, 2, 444);
    explorer = new OSMOExplorer();
    scenario = new Scenario(false);
    scenario.addStartup("start", "increase", "increase", "increase", "increase");
    config.setScenario(scenario);
    config.setFactory(factory);
    config.setMinSuiteLength(5);
    config.setMinTestLength(5);
  }

  @Test
  public void invalidStepsStart() {
    scenario = new Scenario(false);
    scenario.addStartup("hou", "houhou", "increase", "increase", "mo");
    config.setScenario(scenario);
    try {
      explorer.explore(config);
      fail("Starting generation with invalid step definitions should fail");
    } catch (OSMOException e) {
      String expected = "Scenario startup script contains steps not found in model:[hou, houhou, mo]";
      String actual = e.getMessage();
      assertEquals("Failure when startup script has invalid step definitions", expected, actual);
    }
  }

  @Test
  public void startupStrictSlice() {
    scenario = new Scenario(true);
    scenario.addStartup("start", "increase", "increase", "increase", "increase");
    config.setScenario(scenario);
    config.setMinSuiteLength(20);
    scenario.addSlice("increase", 2, 0);
    explorer.explore(config);
    TestSuite suite = explorer.getSuite();
    List<TestCase> tests = suite.getAllTestCases();

    for (TestCase test : tests) {
      List<TestCaseStep> steps = test.getSteps();
      boolean decrease = false;
      boolean increase = false;
      for (TestCaseStep step : steps) {
        String name = step.getName();
        if (name.equals("decrease")) decrease = true;
        if (name.equals("increase")) increase = true;
      }
      assertFalse("Decrease step should not be found in test", decrease);
      assertTrue("Increase step should be found in test", increase);
      String script = (String) test.getAttribute("test-script");
      System.out.println(script);
    }
  }

  @Test
  public void startupSequenceWithAllSlices() {
    scenario.addSlice("decrease", 3, 0);
    scenario.addSlice("increase", 0, 4);
    config.setFailWhenNoWayForward(false);
    explorer.explore(config);
    TestSuite suite = explorer.getSuite();
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
  public void forbidDecrease() throws Exception {
    //needed to have previous tests finish parallel execution of last paths before starting this and static counters..
    Thread.sleep(1000);
    CounterModel.increases = 0;
    CounterModel.decreases = 0;
    config.setMinTestLength(20);
    scenario.forbid("decrease");
    explorer.explore(config);
    TestSuite suite = explorer.getSuite();
    List<TestCase> tests = suite.getAllTestCases();

    boolean decrease = false;
    for (TestCase test : tests) {
      List<TestCaseStep> steps = test.getSteps();
      for (TestCaseStep step : steps) {
        String name = step.getName();
        if (name.equals("decrease")) decrease = true;
      }
    }
    assertFalse("Decrease step should not be found in test", decrease);
    assertEquals("Exploration should not explore forbidden steps (decrease)", 0, CounterModel.decreases);
  }

  private void assertStartSequences() {
    TestSuite suite = explorer.getSuite();
    List<TestCase> tests = suite.getAllTestCases();
    for (TestCase test : tests) {
      assertStartSequence(test);
    }
  }

  private void assertStartSequence(TestCase test) {
    List<TestCaseStep> steps = test.getSteps();
    assertEquals("Step name at index 0", "start", steps.get(0).getName());
    assertEquals("Step name at index 1", "increase", steps.get(1).getName());
    assertEquals("Step name at index 2", "increase", steps.get(2).getName());
    assertEquals("Step name at index 3", "increase", steps.get(3).getName());
    assertEquals("Step name at index 4", "increase", steps.get(4).getName());
  }
}
