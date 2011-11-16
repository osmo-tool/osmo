package osmo.tester.generator.testsuite;

import osmo.common.log.Logger;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
  private List<TestStep> steps = new ArrayList<TestStep>();
  /** The latest test step (being/having been generated). */
  private TestStep currentStep = null;
  /** Unique identifier for this test case. */
  private final int id;
  /** The next identifier in line to set for test cases. */
  private static AtomicInteger nextId = new AtomicInteger(1);
  /** Identifier for next test case step. */
  private int nextStepId = 1;
  /** The data variables and the values covered for each in this test case. */
  private Map<String, ModelVariable> variables = new HashMap<String, ModelVariable>();
  /** For user to store their own information (e.g. script) into the generated test case from the model. */
  private Map<String, Object> attributes = new HashMap<String, Object>();
  /** The time when the test case generation was started. Milliseconds as in System.currentTimInMillis(). */
  private long startTime = 0;
  /** The time when the test case generation was ended. Milliseconds as in System.currentTimInMillis(). */
  private long endTime = 0;

  public TestCase() {
    this.id = nextId.getAndIncrement();
  }

  /** @return Unique id for this test case. */
  public int getId() {
    return id;
  }

  public TestStep getCurrentStep() {
    return currentStep;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public long getDuration() {
    return endTime - startTime;
  }

  /**
   * Adds a new test step.
   *
   * @param transition The transition for the test step.
   * @return The new step object.
   */
  public TestStep addStep(FSMTransition transition) {
    TestStep step = new TestStep(this, transition, nextStepId++);
    log.debug("Added step:" + step);
    steps.add(step);
    currentStep = step;
    return step;
  }

  /**
   * Defines that the current test step covered the given requirement.
   *
   * @param requirement The covered requirement identifier.
   */
  public void covered(String requirement) {
    currentStep.covered(requirement);
  }

  /**
   * Get list of test steps generated (so far) for this test case.
   *
   * @return List of test steps (transitions).
   */
  public List<TestStep> getSteps() {
    return steps;
  }

  public Collection<FSMTransition> getCoveredTransitions() {
    Collection<FSMTransition> transitionCoverage = new HashSet<FSMTransition>();
    for (TestStep teststep : steps) {
      transitionCoverage.add(teststep.getTransition());
    }
    return transitionCoverage;
  }

  public Collection<String> getCoveredRequirements() {
    Collection<String> requirementsCoverage = new HashSet<String>();
    for (TestStep teststep : steps) {
      requirementsCoverage.addAll(teststep.getCoveredRequirements());
    }
    return requirementsCoverage;
  }
  /**
   * Aadds a value for model variable. Means a value that was generated.
   *
   * @param name Name of the variable.
   * @param value The value of the variable.
   * @param merge If true, duplicates are removed.
   */
  public void addVariableValue(String name, Object value, boolean merge) {
    ModelVariable variable = variables.get(name);
    if (variable == null) {
      variable = new ModelVariable(name);
      if (merge) {
        variable.enableMerging();
      }
      variables.put(name, variable);
    }
    log.debug("Variable:" + name + " add value:" + value);
//    if (!merge || !variable.contains(value)) {
//      log.debug("m:"+merge+" c:"+variable.contains(value));
    variable.addValue(value);
//    }
  }

  public void addVariableValue(String name, Object value) {
    addVariableValue(name, value, false);
  }

  public Map<String, ModelVariable> getVariables() {
    return variables;
  }

  public void setAttribute(String name, Object value) {
    attributes.put(name, value);
  }

  public Object getAttribute(String name) {
    return attributes.get(name);
  }

  /**
   * Returns names of all transitions taken in this test case. Duplicates are not removed.
   *
   * @return The names of transitions.
   */
  public Collection<String> getAllTransitionNames() {
    Collection<String> names = new ArrayList<String>();
    for (TestStep teststep : steps) {
      names.add(teststep.getTransition().getName());
    }
    return names;
  }
}
