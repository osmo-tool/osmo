package osmo.tester.generator.testsuite;

import osmo.common.ValuePair;
import osmo.common.log.Logger;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes a single test step in a test case.
 * Difference to {@link osmo.tester.model.FSMTransition} is that this includes the runtime information 
 * such as covered requirements.
 * Model state variable values (annotated with {@link osmo.tester.annotation.Variable}) are stored at the end of
 * the executed test step function.
 *
 * @author Teemu Kanstren
 */
public class TestCaseStep {
  private static final Logger log = new Logger(TestCaseStep.class);
  /** The transition (step) that was taken in this test step. */
  private final String transitionName;
  /** The model object from which the transition (step) was executed. */
  private final String modelObjectName;
  /** Step identifier. */
  private final int id;
  /** The parent test case to which this step belongs. */
  private final TestCase parent;
  /** When was the step execution started? */
  private long startTime = 0;
  /** When did the step execution end? */
  private long endTime = 0;
  /** If the execution of this step threw an exception. */
  private boolean failed;
  /** How much coverage score has been added by this test case, up to this step, to the existing test suite? */
  private int addedCoverage;
  /** If detailed tracking is enabled, this stores values of variables used in this step. */
  private Map<String, String> values;

  /**
   * @param parent     Test case under which this step was executed.
   * @param transition The transition that was taken in this test step.
   * @param id         The identifier for this step.
   */
  public TestCaseStep(TestCase parent, FSMTransition transition, int id) {
    this.parent = parent;
    this.transitionName = transition.getStringName();
    if (transition.getTransition() == null) {
      log.debug("NULL transition object, assuming unit test in progress..");
      this.modelObjectName = transition.toString();
    } else {
      this.modelObjectName = transition.getModelObjectName();
    }
    this.id = id;
  }

  public int getId() {
    return id;
  }

  /**
   * The step name practically equals the transition name executed in this step (including prefix).
   * 
   * @return Step/transition name.
   */
  public String getName() {
    return transitionName;
  }

  @Override
  public String toString() {
    return transitionName;
  }

  /**
   * Sets the execution start time.
   */
  public void start() {
    startTime = System.currentTimeMillis();
  }

  /**
   * Sets the execution end time.
   */
  public void end() {
    endTime = System.currentTimeMillis();
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public String getModelObjectName() {
    return modelObjectName;
  }

  public TestCase getParent() {
    return parent;
  }

  public void setFailed(boolean failed) {
    this.failed = failed;
  }

  public boolean isFailed() {
    return failed;
  }

  public int getAddedCoverage() {
    return addedCoverage;
  }

  public void setAddedCoverage(int addedCoverage) {
    this.addedCoverage = addedCoverage;
  }
  
  public void addValue(String name, String value) {
    if (values == null) values = new LinkedHashMap<>();
    String myValues = values.get(name);
    if (myValues == null) {
      myValues = "";
    } else {
      myValues += ", ";
    }
    myValues += value;
    values.put(name, myValues);
  }

  public Map<String, String> getValues() {
    return values;
  }
  
  public List<ValuePair<String>> getHtmlValues() {
    if (values == null) return Collections.EMPTY_LIST;
    List<ValuePair<String>> pairs = new ArrayList<>();
    for (String key : values.keySet()) {
      pairs.add(new ValuePair<>(key, values.get(key)));
    }
    int max = parent.getParameterCount();
    for (int i = values.size() ; i < max ; i++) {
      pairs.add(new ValuePair<>("", ""));
    }
    return pairs;
  }
}
