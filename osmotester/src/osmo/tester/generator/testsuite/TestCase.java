package osmo.tester.generator.testsuite;

import osmo.common.log.Logger;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class describes a single test case and all test steps that it contains.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class TestCase {
  private static Logger log = new Logger(TestCase.class);
  /** The test steps (taken) for this test case. */
  private List<TestCaseStep> steps = new ArrayList<>();
  /** The latest test step (being/having been generated). */
  private TestCaseStep currentStep = null;
  /** Unique identifier for this test case. */
  private final int id;
  /** The next identifier in line to set for test cases. */
  private static AtomicInteger nextId = new AtomicInteger(1);
  /** Identifier for next test case step. */
  private int nextStepId = 1;
  /** For user to store their own information (e.g. script) into the generated test case from the model. */
  private Map<String, Object> attributes = new HashMap<>();
  /** The time when the test case generation was started. Milliseconds as in System.currentTimInMillis(). */
  private long startTime = 0;
  /** The time when the test case generation was ended. Milliseconds as in System.currentTimInMillis(). */
  private long endTime = 0;
  /** Set to true if during test generation there is an exception thrown. */
  private boolean failed = false;
  private TestCoverage coverage = new TestCoverage();
  private TestCoverage coverageClone = null;

  public TestCase() {
    this.id = nextId.getAndIncrement();
  }

  public static void reset() {
    nextId = new AtomicInteger(1);
  }

  /**
   * Creates a name for use in reporting.
   *
   * @return "Test"+id;
   */
  public String getName() {
    return "Test" + id;
  }

  /** @return Unique id for this test case. */
  public int getId() {
    return id;
  }

  public TestCoverage getCoverage() {
    return coverage;
  }

  /**
   * This is the step currently being generated. Once the test step generation starts (before @Pre is invoked) this
   * is already set to the step to be generated (that is, before the actual step code is executed).
   * 
   * @return Reference to current step information.
   */
  public TestCaseStep getCurrentStep() {
    return currentStep;
  }

  /**
   * @return The max number of variable values observed in this test, meaning the biggest for any step. 
   *         The variables values are those @Variable tagged in model.
   */
  public int getParameterCount() {
    int max = 0;
    for (TestCaseStep step : steps) {
      Map<String, String> values = step.getValues();
      //if step has no parameters this will be null
      if (values == null) continue;
      int ps = values.size();
      if (ps > max) max = ps;
    }
    return max;
  }

  /**
   * Time when test generator started executing code for this step.
   * 
   * @return Milliseconds.
   */
  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  /**
   * Time when test generator finished executing code for this step.
   *
   * @return Milliseconds.
   */
  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  /**
   * Time the test generator took to execute the code for this step (includes @Pre and @Post).
   * 
   * @return Milliseconds.
   */
  public long getDuration() {
    return endTime - startTime;
  }

  /**
   * Adds a new test step.
   *
   * @param transition The transition for the test step.
   * @return The new step object.
   */
  public TestCaseStep addStep(FSMTransition transition) {
    TestCaseStep step = new TestCaseStep(this, transition, nextStepId++);
    log.debug("Added step:" + step);
    steps.add(step);
    currentStep = step;
    return step;
  }

//  /**
//   * Defines that the current test step covered the given requirement.
//   *
//   * @param requirement The covered requirement identifier.
//   */
//  public void covered(String requirement) {
//    currentStep.covered(requirement);
//  }

  /**
   * Get list of test steps generated (so far) for this test case.
   *
   * @return List of test steps.
   */
  public List<TestCaseStep> getSteps() {
    return steps;
  }

  /**
   * Get the list of covered test steps for this test case.
   *
   * @return The list of covered transitions.
   */
  public Collection<String> getCoveredSteps() {
    Collection<String> stepCoverage = new LinkedHashSet<>();
    for (TestCaseStep step : steps) {
      stepCoverage.add(step.getName());
    }
    return stepCoverage;
  }

//  /**
//   * Get the set of unique requirements covered, with duplicates removed.
//   *
//   * @return The list of unique requirements covered.
//   */
//  public Collection<String> getUniqueRequirementCoverage() {
//    Collection<String> requirementsCoverage = new LinkedHashSet<>();
//    for (TestCaseStep step : steps) {
//      requirementsCoverage.addAll(step.getCoveredRequirements());
//    }
//    return requirementsCoverage;
//  }
//
//  /**
//   * Gets the full list of covered requirements, including duplicates, in the order they have been covered by the
//   * different test steps.
//   *
//   * @return The full list of covered requirements, in order.
//   */
//  public Collection<String> getFullRequirementCoverage() {
//    Collection<String> requirementsCoverage = new ArrayList<>();
//    for (TestCaseStep step : steps) {
//      requirementsCoverage.addAll(step.getCoveredRequirements());
//    }
//    return requirementsCoverage;
//  }
//
//  /**
//   * Add a value covered for a variable in this step.
//   * 
//   * @param name Name of variable.
//   * @param value Value covered.
//   */
//  public void addVariableValue(String name, Object value) {
//    addVariableValue(name, value, true);
//  }
//  
//  /**
//   * Adds a value for model variable. Means a value that was generated.
//   *
//   * @param name  Name of the variable.
//   * @param value The value of the variable.
//   * @param merge If true, remove duplicate values.
//   */
//  public void addVariableValue(String name, Object value, boolean merge) {
////    log.debug("Variable:" + name + " add value:" + value);
//    currentStep.addVariableValue(name, value, merge);
//  }

//  /**
//   * Returns list of values covered for different variables, with duplicate values removed.
//   * "Test" refers to collecting values for overall test from all steps, and removing duplicates.
//   * 
//   * @return Covered values for this test, no duplicates.
//   */
//  public Map<String, ModelVariable> getTestVariables() {
//    return getVariables(true);
//  }
//
//  /**
//   * The covered values for all variables. Duplicates included.
//   *
//   * @return Coverage for all variables.
//   */
//  public Map<String, ModelVariable> getStepVariables() {        
//    return getVariables(false);
//  }

//  /**
//   * Get all variable values. User can define if duplicates should be removed or not.
//   * 
//   * @param merge If true, duplicates are removed.
//   * @return Key = variable name, value = variable values.
//   */
//  public Map<String, ModelVariable> getVariables(boolean merge) {
//    Map<String, ModelVariable> result = new LinkedHashMap<>();
//
//    for (TestCaseStep step : steps) {
//      Collection<ModelVariable> variables = step.getValues();
//      for (ModelVariable variable : variables) {
//        String name = variable.getName();
//        ModelVariable stored = result.get(name);
//        if (stored == null) {
//          stored = new ModelVariable(name);
//          result.put(name, stored);
//        }
//        stored.addAll(variable, merge);
//      }
//    }
//    return result;
//  }

    /**
     * Allows setting custom attribute values to store with the generated test case.
     *
     * @param name  Attribute name.
     * @param value Attribute value.
     */
  public void setAttribute(String name, Object value) {
    attributes.put(name, value);
  }

  /**
   * Getter for the custom attributes.
   *
   * @param name Name of attribute to get.
   * @return Its value.
   */
  public Object getAttribute(String name) {
    return attributes.get(name);
  }

  /**
   * Returns names of all steps taken in this test case, in order. Duplicates are not removed.
   *
   * @return The names of steps.
   */
  public List<String> getAllStepNames() {
    List<String> names = new ArrayList<>();
    for (TestCaseStep step : steps) {
      names.add(step.getName());
    }
    return names;
  }

  /**
   * Allows setting the test case as failed.
   * When the test throws, the generator sets this to true.
   * User can also set this to reflect any custom oracles.
   * The value is mainly used for some reporting functionality.
   * 
   * @param failed Fail or not.
   */
  public void setFailed(boolean failed) {
    this.failed = failed;
  }

  public boolean isFailed() {
    return failed;
  }

  @Override
  public String toString() {
    return "TestCase:" + steps.toString();
  }

  public void cloneCoverage() {
    coverageClone = new TestCoverage();
    coverageClone.addCoverage(coverage);
  }
  
  public void switchToClonedCoverage() {
    coverage = coverageClone;
  }
}
