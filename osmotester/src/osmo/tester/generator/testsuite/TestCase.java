package osmo.tester.generator.testsuite;

import osmo.common.log.Logger;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
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
  private static final Logger log = new Logger(TestCase.class);
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
  private Map<String, Object> attributes = new LinkedHashMap<>();
  /** The time when the test case generation was started. Milliseconds as in System.currentTimInMillis(). */
  private long startTime = 0;
  /** The time when the test case generation was ended. Milliseconds as in System.currentTimInMillis(). */
  private long endTime = 0;
  /** Set to true if during test generation there is an exception thrown. */
  private boolean failed = false;
  private TestCoverage coverage = new TestCoverage();
  private TestCoverage coverageClone = null;
  private final long seed;

  public TestCase(long seed) {
    this.id = nextId.getAndIncrement();
    this.seed = seed;
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
  
  public int getLength() {
    return steps.size();
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

  public long getSeed() {
    return seed;
  }
}
