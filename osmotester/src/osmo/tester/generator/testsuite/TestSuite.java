package osmo.tester.generator.testsuite;

import osmo.common.log.Logger;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;
import osmo.tester.coverage.TestCoverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes the test suite being generated.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class TestSuite {
  private static Logger log = new Logger(TestSuite.class);
  /** The current test being generated. */
  private TestCase current = null;
  /** The test cases generated so far, excluding the current test case. */
  private final List<TestCase> testCases = new ArrayList<>();
  /** List of covered transitions and number of how many times it exist in the test suite */
  private Map<String, Integer> stepCoverage = new HashMap<>();
  /** The list of requirements that needs to be covered. */
  private Requirements requirements;
  /** The coverage for this test suite. */
  private TestCoverage coverage = new TestCoverage();

  public TestSuite(TestCoverage coverage) {
    this.coverage = coverage;
  }

  public TestSuite() {
  }

  /** Start a new test case. */
  public TestCase startTest() {
    current = new TestCase(this);
    current.setStartTime(System.currentTimeMillis());
    return current;
  }

  /**
   * Resets the suite to avoid memory leaks if test generator is used to produce long test sets,
   * for example, in suite optimization when tests can be generated in phases.
   */
  public void reset() {
    current = null;
    testCases.clear();
    stepCoverage.clear();
  }

  /** End the current test case and moves it to the suite "history". */
  public void endTest() {
    current.setEndTime(System.currentTimeMillis());
    testCases.add(current);
    coverage.addTestCoverage(current);
    current = null;
  }

  /**
   * Adds the given tests to the suite, including coverage.
   *
   * @param tests The test to add to the suite.
   */
  public void addTestCases(List<TestCase> tests) {
    testCases.addAll(tests);
    for (TestCase test : tests) {
      coverage.addTestCoverage(test);
    }
  }

  /**
   * Adds the given test to the suite, including coverage.
   *
   * @param test to add.
   */
  public void addTestCase(TestCase test) {
    testCases.add(test);
    coverage.addTestCoverage(test);
  }

  /**
   * Adds the given transition as a step into the current test case.
   *
   * @param transition The transition to add.
   * @return The added step object.
   */
  public TestCaseStep addStep(FSMTransition transition) {
    TestCaseStep step = current.addStep(transition);
    Integer count = stepCoverage.get(transition.getStringName());
    if (count == null) {
      count = 0;
    }
    stepCoverage.put(transition.getStringName(), count + 1);
    return step;
  }

  /**
   * Marks the given requirement as covered by the current test case.
   *
   * @param requirement The requirement identifier.
   */
  public void covered(String requirement) {
    if (current != null) {
      //this is a special case when an optimizer updates the set in the end
      current.covered(requirement);
    }
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
   * Gives the test cases in this test suite. Excludes the currently generated test case (if not yet finished).
   *
   * @return The test cases.
   */
  public List<TestCase> getFinishedTestCases() {
    return testCases;
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
   * Gives all transitions in this test suite, including coverage number
   * Coverage number tells how many times transition is covered in this test suite
   *
   * @return The transitions with coverage number
   */
  public Map<String, Integer> getStepCoverage() {
    return stepCoverage;
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
    if (current != null && testContains(current, transition)) {
      return true;
    }
    return false;
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
   * Coverage of variables and their values for all test cases in this test suite.
   * Duplicates are removed to give a general overview of all tests coverage together.
   *
   * @return [variable name, variable coverage] mapping.
   */
  public Map<String, ModelVariable> getTestVariables() {
    Map<String, ModelVariable> variables = new HashMap<>();
    List<TestCase> tests = getAllTestCases();
    for (TestCase test : tests) {
      Map<String, ModelVariable> testVariables = test.getTestVariables();
      for (ModelVariable testVar : testVariables.values()) {
        String name = testVar.getName();
        ModelVariable var = variables.get(name);
        if (var == null) {
          var = new ModelVariable(name);
          variables.put(name, var);
        }
        var.addAll(testVar, true);
      }
    }
    return variables;
  }

  /**
   * Coverage of variables and their values for all test steps in this test suite.
   * Duplicates are not removed.
   *
   * @return [variable name, variable coverage] mapping.
   */
  public Map<String, ModelVariable> getStepVariables() {
    Map<String, ModelVariable> variables = new HashMap<>();
    List<TestCase> tests = getAllTestCases();
    for (TestCase test : tests) {
      Map<String, ModelVariable> testVariables = test.getStepVariables();
      for (ModelVariable testVar : testVariables.values()) {
        String name = testVar.getName();
        ModelVariable var = variables.get(name);
        if (var == null) {
          var = new ModelVariable(name);
          variables.put(name, var);
        }
        var.addAll(testVar, false);
      }
    }
    return variables;
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
      log.debug("No requirements object defined. Creating new.");
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
    current.addVariableValue(inputName, value, false);
  }
}
