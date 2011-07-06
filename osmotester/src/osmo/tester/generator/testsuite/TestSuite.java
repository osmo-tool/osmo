package osmo.tester.generator.testsuite;

import osmo.tester.model.FSMTransition;
import osmo.tester.model.Requirements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
  private final List<TestCase> testCases = new ArrayList<TestCase>();
  /** List of covered requirements so far, excluding the current test case under generation. */
  private final Collection<String> coveredRequirements = new HashSet<String>();
  /** List of covered transitions so far, excluding the current test case under generation. */
  private final Collection<FSMTransition> coveredTransitions = new HashSet<FSMTransition>();

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
    testCases.add(current);
    current = null;
  }

  /**
   * Calculates the difference between the requirements and transitions covered in previous test cases
   * (stored in \code{testCases}) and the current (last) generated test case. Stores the differences in
   * the test case object for the currently generated test case.
   */
  private void computeAddedCoverage() {
    //first we create a list of requirements covered by the previous test cases
    Collection<String> oldRequirementCoverage = new ArrayList<String>();
    //and the same for the transitions
    Collection<FSMTransition> oldTransitionCoverage = new ArrayList<FSMTransition>();
    for (TestCase testCase : testCases) {
      List<TestStep> steps = testCase.getSteps();
      for (TestStep step : steps) {
        oldRequirementCoverage.addAll(step.getCoveredRequirements());
        oldTransitionCoverage.add(step.getTransition());
      }
    }

    //now we find the difference to the new coverage in the current test case
    List<TestStep> steps = current.getSteps();
    Collection<String> newRequirementCoverage = new ArrayList<String>();
    Collection<FSMTransition> newTransitionCoverage = new ArrayList<FSMTransition>();
    for (TestStep step : steps) {
      Collection<String> covered = step.getCoveredRequirements();
      newRequirementCoverage.addAll(covered);
      newTransitionCoverage.add(step.getTransition());
    }
    //At this point we have a list of all covered requirements/transitions for the current test case, including those
    //already covered by the previous test cases. So we retain only the diff.
    newRequirementCoverage.removeAll(oldRequirementCoverage);
    newTransitionCoverage.removeAll(oldTransitionCoverage);

    current.setAddedRequirementsCoverage(newRequirementCoverage);
    current.setAddedTransitionCoverage(newTransitionCoverage);
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
  public TestCase getCurrent() {
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

}
