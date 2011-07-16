package osmo.tester.generator.testsuite;

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
 * This also includes a list of added coverage for model transitions and requirements.
 * Note that this coverage information may or may not hold in current testing.
 * Note that this information needs to be updated if optimization to test suite ordering etc. is applied
 * after the suite has been generated.
 * All optimizers provided with OSMOTester will also update the added coverage information of the tests.
 *
 * @author Teemu Kanstren
 */
public class TestCase {
  /** The test steps (taken) for this test case. */
  private List<TestStep> steps = new ArrayList<TestStep>();
  /** The latest test step (being/having been generated). */
  private TestStep currentStep = null;
  /** Newly covered transitions in relation to generation history. See class header for notes.
  *  NOTE: we use a Set to avoid duplicates if the same transition is covered multiple times. */
  private Collection<FSMTransition> addedTransitionCoverage = new HashSet<FSMTransition>();
  /** Newly covered requirements in relation to generation history. See class header for notes.
   *  NOTE: we use a Set to avoid duplicates if the same requirement is covered multiple times. */
  private Collection<String> addedRequirementsCoverage = new HashSet<String>();
  /** Unique identifier for this test case. */
  private final int id;
  /** Was this test case executed as a success or failure? This flag is only for reporting and practically
   * useful only for online testing. */
  private boolean success = true;
  /** Test script can be stored here. For optimization, reporting, etc. support. */
  private String script = "";
  /** Support for the user to store any properties they wish for specific test case. */
  private final Map<String, Object> properties = new HashMap<String, Object>();
  /** The next identifier in line to set for test cases. */
  private static AtomicInteger nextId = new AtomicInteger(1);

  public TestCase() {
    this.id = nextId.getAndIncrement();
  }

  /**
   * @return Unique id for this test case.
   */
  public int getId() {
    return id;
  }

  /**
   * @return True if test case was a success (generation or execution, choice is up to user).
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Causes this test case to be set as a failure.
   */
  public void fail() {
    this.success = false;
  }

  /**
   * Sets a property for this test case to the given value.
   * Here simply to support user in storing metadata for tests.
   *
   * @param key Name of the property.
   * @param value Value for the property.
   */
  public void setProperty(String key, Object value) {
    properties.put(key, value);
  }

  /**
   * @param key The name of the property to get value for.
   * @return User set value for the property, null if none.
   */
  public Object getProperty(String key) {
    return properties.get(key);
  }

  /**
   * @return User defined value for test script.
   */
  public String getScript() {
    return script;
  }

  /**
   * @param script New value for the test script.
   */
  public void setScript(String script) {
    this.script = script;
  }

  /**
   * Adds a new test step.
   *
   * @param transition The transition for the test step. 
   */
  public void addTransition(FSMTransition transition) {
    TestStep step = new TestStep(transition);
    steps.add(step);
    currentStep = step;
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

  /**
   * Clear list of added transitions and requirements coverage.
   * Useful in test suite optimization when these lists need to be updated.
   */
  public void resetCoverage() {
    addedRequirementsCoverage.clear();
    addedTransitionCoverage.clear();
  }

  public Collection<FSMTransition> getAddedTransitionCoverage() {
    return addedTransitionCoverage;
  }

  public void addAddedTransitionCoverage(FSMTransition transition) {
    addedTransitionCoverage.add(transition);
  }

  public Collection<String> getAddedRequirementsCoverage() {
    return addedRequirementsCoverage;
  }

  public void addAddedRequirementsCoverage(String requirement) {
    addedRequirementsCoverage.add(requirement);
  }

  @Override
  public String toString() {
    return "TestCase{" +
            "steps=" + steps +
            '}';
  }
}
