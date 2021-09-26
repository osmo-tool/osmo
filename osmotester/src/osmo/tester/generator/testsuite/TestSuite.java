package osmo.tester.generator.testsuite;

//import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import osmo.common.Logger;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;
import osmo.tester.model.VariableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes the test suite being generated.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class TestSuite {
  private static final Logger log = new Logger(TestSuite.class);
  /** The current test being generated. */
  private TestCase current = null;
  /** The test cases generated so far, excluding the current test case. */
  private final List<TestCase> testCases = new ArrayList<>();
  /** The list of requirements that needs to be covered. */
  private Requirements requirements;
  /** The coverage for this test suite. */
  private TestCoverage coverage = new TestCoverage();
  /** Coverage for current test case under generation. */
  private TestCoverage testCoverage = null;
  /** Whether we should track variable parameters or not. */
  private boolean trackParameters = false;
  /** Keep generated tests in history or dump them? For large scale generation is required. */
  private boolean keepTests = true;
  /** Number of tests that have passed through. Even if tests are not kept. And note that parallel suites will differ.. */
  private int testCount = 0;
  /** Has the suite been finished? */
  private boolean ended = false;
  /** Timing statistics for different steps in generation. */
  private Map<String, TestStatistics> stepStats = new HashMap<>();
  /** Timing statistics for different test in generation. */
  private TestStatistics suiteStats = new TestStatistics();

  public TestSuite(TestCoverage coverage) {
    this.coverage = coverage;
  }

  public TestSuite() {
  }

  public boolean isEnded() {
    return ended;
  }

  public void setEnded(boolean ended) {
    this.ended = ended;
  }

  public void setKeepTests(boolean keepTests) {
    this.keepTests = keepTests;
  }

  /**
   * Gives the number of the test case currently being generated.
   *
   * @return Number of previous tests in suite + 1;
   */
  public int currentTestNumber() {
    return testCount;
  }

  /**
   * Start a new test case.
   *
   * @param seed Randomization seed for new test case.
   * @return The test case object to use for generating new test case.
   */
  public TestCase startTest(long seed) {
    current = new TestCase(seed);
    current.setStartTime(System.currentTimeMillis());
    testCoverage = current.getCoverage();
    return current;
  }

  /** End the current test case and moves it to the suite "history". */
  public void endTest() {
    testCount++;
    current.setEndTime(System.currentTimeMillis());
    if (keepTests) testCases.add(current);
    coverage.addCoverage(testCoverage);
    long duration = current.getDuration();
    suiteStats.addValue(duration);
    current = null;
  }

  /**
   * Adds the given transition as a step into the current test case.
   *
   * @param transition The transition to add.
   * @return The added step object.
   */
  public TestCaseStep addStep(FSMTransition transition) {
    testCoverage.addStep(transition.getStringName());
    return current.addStep(transition);
  }

  /**
   * Counts the total number of tests in the test suite and the test case currently being generated.
   *
   * @return The total number of test steps in test suite.
   */
  public int totalSteps() {
    int count = 0;
    for (TestCase test : testCases) {
      count += test.getSteps().size();
    }
    //current is null when suite is initialized but no tests are started
    if (current != null) {
      count += current.getSteps().size();
    }
    return count;
  }

  /**
   * Access to the test case being currently generated.
   *
   * @return The current test case.
   */
  public TestCase getCurrentTest() {
    return current;
  }

  /**
   * Access to the last test case generated so far.
   *
   * @return The last test case so far.
   */
  public TestCase getLastTest() {
    return testCases.get(testCases.size() - 1);
  }

  /**
   * Gives all test cases in this test suite, including the one being currently generated.
   *
   * @return The test cases.
   */
  public List<TestCase> getAllTestCases() {
    List<TestCase> all = new ArrayList<>(testCases.size() + 1);
    all.addAll(testCases);
    if (current != null) {
      //current is null if we finished test generation
      all.add(current);
    }
    return all;
  }

  /**
   * Gives the number of test steps in the current test case.
   *
   * @return The number of test steps in the current test case.
   */
  public int currentSteps() {
    //current is null before starting the first test case
    if (current == null) {
      return 0;
    }
    return current.getSteps().size();
  }

  /**
   * Checks if the given transition is present in any of the previously generated test cases (history+current test case).
   *
   * @param transition The transition to check.
   * @return True if transition is present, false if not.
   */
  public boolean contains(FSMTransition transition) {
    for (TestCase testCase : testCases) {
      if (testContains(testCase, transition)) {
        return true;
      }
    }
      return current != null && testContains(current, transition);
  }

  /**
   * Checks if the given test case contains a step executing the given transition.
   *
   * @param testCase   The test case to check.
   * @param transition The transition to check.
   * @return True if the given transition is found in the given test case, otherwise false.
   */
  private boolean testContains(TestCase testCase, FSMTransition transition) {
    List<TestCaseStep> steps = testCase.getSteps();
    for (TestCaseStep step : steps) {
      if (step.getName().equals(transition.getStringName())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Create requirements object if user did not provide one, and initialize whichever one is used with required values.
   *
   * @param requirements User provided requirements, if any.
   */
  public void initRequirements(Requirements requirements) {
    if (requirements == null) {
      //the requirements are initialized if an instance was found in the model objects
      //otherwise we create a new one so functionality for requirements checks does not crash
      log.d("No requirements object defined. Creating new.");
      requirements = new Requirements();
    }
    this.requirements = requirements;
    requirements.setTestSuite(this);
  }

  public Requirements getRequirements() {
    return requirements;
  }

  public TestCoverage getCoverage() {
    return coverage;
  }

  /**
   * Add the given value to the variable with the given name for the current step.
   *
   * @param inputName The name of the variable.
   * @param value     The value to add for the variable.
   */
  public void addValue(String inputName, Object value) {
    String strValue = "" + value;
    testCoverage.addVariableValue(inputName, strValue);
    if (trackParameters) current.getCurrentStep().addValue(inputName, strValue);
  }

  public void addUserCoverage(String name, String value) {
    testCoverage.addUserCoverage(name, value);
  }

  /**
   * Stores the general step state. General state refers to the state in the model variables that the generator sees.
   * Another state is the user defined state, which is queried from specifically annotated methods with
   * {@link osmo.tester.annotation.CoverageValue}.
   *
   * @param fsm We grab references to the state objects from here.
   */
  public void storeGeneralState(FSM fsm) {
    Collection<VariableField> variables = fsm.getModelVariables();
    for (VariableField variable : variables) {
      String name = variable.getName();
      Object value = variable.getValue();
      if (!variable.isSearchableInput()) {
        addValue(name, "" + value);
      }
    }
  }

  public void coveredRequirement(String name) {
    testCoverage.coveredRequirement(name);
  }

  public void enableParameterTracking() {
    trackParameters = true;
  }

  /**
   * Add timing stats for given test, calculating from its start and end time.
   *
   * @param step To add stats for.
   */
  public void addStepStat(TestCaseStep step) {
    TestStatistics stats = stepStats.computeIfAbsent(step.getName(), n -> new TestStatistics());
    long diff = step.getEndTime() - step.getStartTime();
    stats.addValue(diff);
    getCurrentTest().addStepStat(step);
  }

  /**
   * @return Timing statisctics for all steps. Key = step name, value = stats for the step.
   */
  public Map<String, TestStatistics> getStepStats() {
    return stepStats;
  }

  /**
   * @param stepName To get stats for.
   * @return Stats for step with given name, null if not found.
   */
  public TestStatistics statsForStep(String stepName) {
    return stepStats.get(stepName);
  }

  /**
   * @return Overall timing statistics for all tests.
   */
  public TestStatistics getSuiteStats() {
    return suiteStats;
  }
}
