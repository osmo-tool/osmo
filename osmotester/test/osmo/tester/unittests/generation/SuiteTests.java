package osmo.tester.unittests.generation;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.assertj.core.data.Percentage;
import org.junit.Test;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

/** @author Teemu Kanstren */
public class SuiteTests {
  @Test
  public void validSuite() {
    TestSuite suite = new TestSuite();
    suite.startTest(1);
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    suite.endTest();
    suite.startTest(1);
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    assertEquals("Number of test cases in test suite", 2, suite.getAllTestCases().size());
    assertEquals("Number of steps in current test", 2, suite.currentSteps());
    assertEquals("Number of total steps in test suite", 4, suite.totalSteps());
    suite.endTest();
    assertEquals("Number of steps in current test", 0, suite.currentSteps());
    assertEquals("Number of test cases in test suite", 2, suite.getAllTestCases().size());
  }

  @Test
  public void totalCoverage() {
    TestSuite suite = new TestSuite();
    suite.startTest(1);
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    suite.coveredRequirement("r1");

    int a1 = suite.getCurrentTest().getCoverage().getRequirements().size();
    assertEquals("Coverage for requirements", 1, a1);
    int a2 = suite.getCurrentTest().getCoveredSteps().size();
    assertEquals("Coverage for transitions", 2, a2);
    suite.endTest();

    suite.startTest(1);
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t2"));
    suite.coveredRequirement("r1");

    a1 = suite.getCurrentTest().getCoverage().getRequirements().size();
    assertEquals("Added coverage for requirements", 1, a1);
    a2 = suite.getCurrentTest().getCoveredSteps().size();
    assertEquals("Added coverage for transitions", 2, a2);
    suite.endTest();

    suite.startTest(1);
    suite.addStep(new FSMTransition("t1"));
    suite.addStep(new FSMTransition("t3"));
    suite.coveredRequirement("r2");

    a1 = suite.getCurrentTest().getCoverage().getRequirements().size();
    assertEquals("Coverage for requirements", 1, a1);
    a2 = suite.getCurrentTest().getCoveredSteps().size();
    assertEquals("Coverage for transitions", 2, a2);
    suite.endTest();

    suite.startTest(1);
    suite.addStep(new FSMTransition("t3"));
    suite.addStep(new FSMTransition("t4"));
    suite.coveredRequirement("r3");
    suite.addStep(new FSMTransition("t5"));
    suite.coveredRequirement("r4");

    a1 = suite.getCurrentTest().getCoverage().getRequirements().size();
    assertEquals("Coverage for requirements", 2, a1);
    a2 = suite.getCurrentTest().getCoveredSteps().size();
    assertEquals("Coverage for transitions", 3, a2);
    suite.endTest();

    assertEquals("Number of steps in current test", 0, suite.currentSteps());
    assertEquals("Number of test cases in test suite", 4, suite.getAllTestCases().size());
  }

  @Test
  public void historyContainsByName() {
    TestSuite suite = new TestSuite();
    suite.startTest(1);
    suite.addStep(new FSMTransition("bob"));
    suite.addStep(new FSMTransition("alice"));
    suite.endTest();
    suite.startTest(1);
    suite.addStep(new FSMTransition("bob2"));
    suite.addStep(new FSMTransition("alice2"));
    suite.endTest();
    suite.startTest(1);
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
    suite.startTest(1);
    suite.addStep(new FSMTransition("bob"));
    suite.addStep(new FSMTransition("alice"));
    assertTrue(suite.contains(new FSMTransition("bob")));
    assertFalse(suite.contains(new FSMTransition("bob4")));
  }

  @Test
  public void customAttributes() {
    TestCase test = new TestCase(1);
    test.setAttribute("script", "whooppee");
    assertEquals("Stored attribute in test case", "whooppee", test.getAttribute("script"));
  }
  
  @Test
  public void addSingleValue() {
    TestSuite suite = new TestSuite();
    suite.enableParameterTracking();
    suite.startTest(1);
    suite.addStep(new FSMTransition("bob"));
    suite.addValue("var1", "value1");
    suite.addStep(new FSMTransition("alice"));
    suite.endTest();
    TestCase testCase = suite.getAllTestCases().get(0);
    List<TestCaseStep> steps = testCase.getSteps();
    TestCaseStep step = steps.get(0);
    Map<String,String> values = step.getValues();
    String var1 = values.get("var1");
    assertEquals("Stored value for var1", "value1", var1);
  }

  @Test
  public void addSeveralValues() {
    TestSuite suite = new TestSuite();
    suite.enableParameterTracking();
    suite.startTest(1);
    suite.addStep(new FSMTransition("bob"));
    suite.addValue("var1", "value1.1");
    suite.addValue("var2", "value2.1");
    suite.addStep(new FSMTransition("alice"));
    suite.addValue("var1", "value1.2");
    suite.addValue("var1", "value1.3");
    suite.addValue("var2", "value2.1");
    suite.endTest();
    TestCase testCase = suite.getAllTestCases().get(0);
    List<TestCaseStep> steps = testCase.getSteps();

    TestCaseStep step = steps.get(0);
    Map<String,String> values = step.getValues();
    String var1 = values.get("var1");
    assertEquals("Stored value for var1", "value1.1", var1);
    String var2 = values.get("var2");
    assertEquals("Stored value for var2", "value2.1", var2);

    TestCaseStep step2 = steps.get(1);
    values = step2.getValues();
    var1 = values.get("var1");
    assertEquals("Stored value for var1", "value1.2, value1.3", var1);
    var2 = values.get("var2");
    assertEquals("Stored value for var2", "value2.1", var2);
  }

  @Test
  public void stats() {
    TestSuite suite = new TestSuite();
    suite.startTest(1);
    addStep(suite, "step1", 100, 200);
    assertSuiteStepStat(suite, "step1", 100, 0);
    assertTestStepStat(suite, "step1", 100, 0);

    addStep(suite, "step2", 200, 250);
    assertSuiteStepStat(suite, "step2", 50, 0);
    assertTestStepStat(suite, "step2", 50, 0);

    suite.endTest();
    suite.startTest(2);
    addStep(suite, "step1", 500, 700);
    assertSuiteStepStat(suite, "step1", 150, 70.7);
    assertTestStepStat(suite, "step1", 200, 0);

  }

  private void addStep(TestSuite suite, String name, int start, int end) {
    TestCaseStep step = suite.addStep(new FSMTransition(name));
    step.setStartTime(start);
    step.setEndTime(end);
    suite.addStepStat(step);
  }

  private void assertSuiteStepStat(TestSuite suite, String name, double mean, double std) {
    SummaryStatistics stats = suite.statsForStep(name);
    assertThat(stats).isNotNull();
    assertThat(stats.getMean()).isEqualTo(mean);
    assertThat(stats.getStandardDeviation()).isCloseTo(std, Percentage.withPercentage(0.02));
  }

  private void assertTestStepStat(TestSuite suite, String name, double mean, double std) {
    SummaryStatistics stats = suite.getCurrentTest().statsFor(name);
    assertThat(stats).isNotNull();
    assertThat(stats.getMean()).isEqualTo(mean);
    assertThat(stats.getStandardDeviation()).isCloseTo(std, Percentage.withPercentage(0.02));
  }
}
