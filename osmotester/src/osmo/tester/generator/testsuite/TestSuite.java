package osmo.tester.generator.testsuite;

import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes the test suite being generated.
 * 
 * @author Teemu Kanstren
 */
public class TestSuite {
  /** The current test being generated. */
  private TestCase current = null;
  /** The test cases generated so far, excluding the current test case. */
  private List<TestCase> history = new ArrayList<TestCase>();

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
    history.add(current);
    current = null;
  }

  /**
   * Adds the given transition as a step into the current test case.
   *
   * @param transition The transition to add.
   */
  public void add(FSMTransition transition) {
    current.addTransition(transition);
  }

  /**
   * Marks the given requirement as covered by the current test case.
   *
   * @param requirement The requirement identifier.
   */
  public void covered(String requirement) {
    current.covered(requirement);
  }

  /**
   * Counts the total number of tests in the test suite and the test case currently being generated.
   *
   * @return The total number of test steps in test suite.
   */
  public int totalSteps() {
    int count = 0;
    for (TestCase test : history) {
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
  public TestCase getCurrent() {
    return current;
  }

  /**
   * Access to the overall test case generation history. Excludes the currently generated test case.
   *
   * @return The history access object.
   */
  public List<TestCase> getHistory() {
    return history;
  }

  public List<TestCase> getAll() {
    List<TestCase> all = new ArrayList<TestCase>(history.size()+1);
    all.addAll(history);
    all.add(current);
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
    for (TestCase testCase : history) {
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
}
