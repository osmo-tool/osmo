package osmo.tester.generator.testsuite;

import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Describes the test suite being generated.
 * 
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class TestSuite {
  /** The current test being generated. */
  private TestCase current = null;
  /** The test cases generated so far, excluding the current test case. */
  private final List<TestCase> testCases = new ArrayList<TestCase>();
  /** List of covered requirements so far, excluding the current test case under generation. */
  private final Collection<String> coveredRequirements = new HashSet<String>();
  /** List of covered transitions so far, excluding the current test case under generation. */
  private final Collection<FSMTransition> coveredTransitions = new HashSet<FSMTransition>();
  /** List of covered transitions and number of how many times it exist in the test suite*/
  private Map<FSMTransition, Integer> transitionCoverage = new HashMap<FSMTransition, Integer>();
  /** List of covered transitions pairs and number of how many times it exist in the test suite*/
  private Map<FSMTransition, Integer> tpCoverage = new HashMap<FSMTransition, Integer>();

  /**
   * Start a new test case.
   */
  public void startTest() {
    current = new TestCase();
  }

  /**
   * End the current test case and moves it to the suite "history".
   */
  public void endTest() {
    coveredRequirements.addAll(current.getAddedRequirementsCoverage());
    coveredTransitions.addAll(current.getAddedTransitionCoverage());
    testCases.add(current);
    current = null;
  }

  /**
   * Adds the given transition as a step into the current test case.
   *
   * @param transition The transition to add.
   * @return The added step object.
   */
  public TestStep addStep(FSMTransition transition) {
    TestStep step = current.addStep(transition);
    if (!coveredTransitions.contains(transition)) {
      current.addAddedTransitionCoverage(transition);
    }
    Integer count = transitionCoverage.get(transition);
    if (count == null) {
      count = 0;
    }
    transitionCoverage.put(transition, count+1);
    return step;
  }

  /**
   * Marks the given requirement as covered by the current test case.
   *
   * @param requirement The requirement identifier.
   */
  public void covered(String requirement) {
    current.covered(requirement);
    if (!coveredRequirements.contains(requirement)) {
      current.addAddedRequirementsCoverage(requirement);
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
  public List<TestCase> getTestCases() {
    return testCases;
  }

  /**
   * Gives all test cases in this test suite, including the one being currently generated.
   *
   * @return The test cases.
   */
  public List<TestCase> getAllTestCases() {
    List<TestCase> all = new ArrayList<TestCase>(testCases.size()+1);
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
  public Map<FSMTransition, Integer> getTransitionCoverage(){
    return transitionCoverage;
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
   * Checks if the given test case contains the given transition.
   *
   * @param testCase The test case to check.
   * @param transition The transition to check.
   * @return True if the given transition is found in the given test case, otherwise false.
   */
  private boolean testContains(TestCase testCase, FSMTransition transition) {
    List<TestStep> steps = testCase.getSteps();
    for (TestStep step : steps) {
      if (step.getTransition().equals(transition)) {
        return true;
      }
    }
    return false;
  }

  public void setStepProperty(String name, Object value) {
    current.setStepProperty(name, value);
  }

}
