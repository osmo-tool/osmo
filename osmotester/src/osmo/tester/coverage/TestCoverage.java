package osmo.tester.coverage;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Represents test coverage for a set of test cases.
 * Used to calculate coverage scores for test suite optimization.
 *
 * @author Teemu Kanstren
 */
public class TestCoverage {
  private static final Logger log = new Logger(TestCoverage.class);
  /** Number of times each step has been covered. */
  private Map<String, Integer> stepCount = new LinkedHashMap<>();
  /** The set of step pairs covered. */
  private Collection<String> stepPairs = new LinkedHashSet<>();
  /** The unique set of steps covered. */
  private Collection<String> singles = new LinkedHashSet<>();
  /** The set of covered requirements. */
  private Collection<String> reqs = new LinkedHashSet<>();
  /** The set of covered variables. */
  private Collection<String> variables = new LinkedHashSet<>();
  /** Set of values covered for different model data variables. */
  private Map<String, Collection<String>> variableValues = new LinkedHashMap<>();
  /** Set of covered coverage values (user coverage values). */
  private Map<String, Collection<String>> coverageValues = new LinkedHashMap<>();
  /** Set of covered transitions between coverage values, or pairs of observed user coverage values. */
  private Map<String, Collection<String>> coverageValuePairs = new LinkedHashMap<>();
  /** Previously taken step, used for pair coverage. */
  private String previousStep = FSM.START_STEP_NAME;
  /** Previous values for user defined coverage values. Used for pair coverage. */
  private Map<String, String> previousCoverageValues = new LinkedHashMap<>();
  /** Total number of steps taken in all tests. */
  private int totalSteps = 0;

  /** Start with an empty set. */
  public TestCoverage() {
  }

  public TestCoverage(Collection<TestCase> tests) {
    for (TestCase test : tests) {
      addCoverage(test.getCoverage());
    }
  }

  public synchronized void addVariableValue(String name, String value) {
    checkAndAdd(name, value, variableValues);
    variables.add(name);
  }

  public synchronized void addUserCoverage(String name, String value) {
    checkAndAdd(name, value, coverageValues);
    String previousState = previousCoverageValues.get(name);
    if (previousState == null) previousState = "osmo.tester.START_STATE";
    String pair = previousState + "->" + value;
    previousCoverageValues.put(name, value);
    checkAndAdd(name + "-pair", pair, coverageValuePairs);
  }

  private void checkAndAdd(String name, String value, Map<String, Collection<String>> to) {
    Collection<String> values = to.get(name);
    if (values == null) {
      values = new LinkedHashSet<>();
      to.put(name, values);
    }
    values.add(value);
  }

  /**
   * Gives names of all steps and number of times each is covered in whatever this object is tracking.
   *
   * @return The test steps with coverage number.
   */
  public Map<String, Integer> getStepCoverage() {
    return stepCount;
  }

  /** @param name The name of step to add. */
  public synchronized void addStep(String name) {
    increaseStepCount(name, 1);
//    steps.add(name);
    singles.add(name);
    stepPairs.add(previousStep + "->" + name);
    previousStep = name;
    totalSteps++;
  }

  private void increaseStepCount(String name, int by) {
    Integer count = stepCount.get(name);
    if (count == null) count = 0;
    stepCount.put(name, count+by);
  }

  public int getTotalSteps() {
    return totalSteps;
  }

  public Collection<String> getStepPairs() {
    return stepPairs;
  }

  public Collection<String> getSingles() {
    return singles;
  }

  public Collection<String> getRequirements() {
    return reqs;
  }

  public Collection<String> getVariables() {
    return variables;
  }

  /** @return Values for variables. */
  public Map<String, Collection<String>> getVariableValues() {
    return variableValues;
  }

  /**
   * Get the overall number of all values for all variables.
   *
   * @return The number, what else?
   */
  public int getValueCount() {
    int count = 0;
    for (Collection<String> strings : variableValues.values()) {
      count += strings.size();
    }
    return count;
  }

  public Map<String, Collection<String>> getStates() {
    return coverageValues;
  }

  /**
   * Get the number of coverage values observed.
   * This is the total sum of all values observed for all coverage value methods.
   *
   * @return The count, as explained above.
   */
  public int getStateCount() {
    int count = 0;
    for (Collection<String> strings : coverageValues.values()) {
      count += strings.size();
    }
    return count;
  }

  public Map<String, Collection<String>> getStatePairs() {
    return coverageValuePairs;
  }

  /**
   * Get the number of coverage value pairs observed.
   * This is the total sum of all value pairs observed for all coverage value methods.
   *
   * @return The count, as explained above.
   */
  public int getStatePairCount() {
    int count = 0;
    for (Collection<String> strings : coverageValuePairs.values()) {
      count += strings.size();
    }
    return count;
  }

  /**
   * Creates a clone of this coverage set.
   * The clone can then be modified without affecting the original set (no reference to same internal lists etc.).
   *
   * @return The clone object.
   */
  public synchronized TestCoverage cloneMe() {
    TestCoverage clone = new TestCoverage();
    addCoverage(this, clone);
    return clone;
  }

  public synchronized void addCoverage(TestCoverage from) {
    addCoverage(from, this);
  }

  /**
   * This is only called to add coverage to a test suite coverage, meaning we can add all together every time.
   *
   * @param from Add from here.
   * @param to   Add to here.
   */
  private static void addCoverage(TestCoverage from, TestCoverage to) {
    to.stepPairs.addAll(from.stepPairs);
    to.totalSteps += from.totalSteps;
    for (String name : from.stepCount.keySet()) {
      to.increaseStepCount(name, from.stepCount.get(name));
    }

//    to.steps.addAll(from.steps);
    to.reqs.addAll(from.reqs);
    to.singles.addAll(from.singles);
    to.variables.addAll(from.variables);
    for (String key : from.variableValues.keySet()) {
      Collection<String> values = to.variableValues.get(key);
      if (values == null) {
        values = new LinkedHashSet<>();
        to.variableValues.put(key, values);
      }
      values.addAll(from.variableValues.get(key));
    }
    for (String key : from.coverageValues.keySet()) {
      Collection<String> values = to.coverageValues.get(key);
      if (values == null) {
        values = new LinkedHashSet<>();
        to.coverageValues.put(key, values);
      }
      values.addAll(from.coverageValues.get(key));
    }
    for (String key : from.coverageValuePairs.keySet()) {
      Collection<String> values = to.coverageValuePairs.get(key);
      if (values == null) {
        values = new LinkedHashSet<>();
        to.coverageValuePairs.put(key, values);
      }
      values.addAll(from.coverageValuePairs.get(key));
    }
  }

  @Override
  public String toString() {
    return "TestCoverage{" +
            "totalSteps=" + totalSteps +
            ", stepPairs=" + stepPairs +
            ", singles=" + singles +
            ", reqs=" + reqs +
            ", variables=" + variables +
            ", variableValues=" + variableValues +
            ", coverageValues=" + coverageValues +
            ", coverageValuePairs=" + coverageValuePairs +
            '}';
  }

  /**
   * Produces a human-readable string to present coverage data.
   *
   * @param fsm               Our model.
   * @param possibleStepPairs List of possible step-pairs (all that could be covered).
   * @param possibleValues    List of possible variable values.
   * @param possibleCVs       List of possible coverage values.
   * @param possibleCVPairs   List of possible coverage value pairs.
   * @param printAll          If true, we print a list of names for missing coverage elements.
   * @return The created string. Simple ASCII text on several lines.
   */
  public String coverageString(FSM fsm, Collection<String> possibleStepPairs, Map<String, Collection<String>> possibleValues,
                               Map<String, Collection<String>> possibleCVs, Map<String, Collection<String>> possibleCVPairs,
                               boolean printAll) {
    String result = "Covered elements:\n";
    result += "Total steps: " + totalSteps;
    result = stepCoverageString(fsm, result);
    result = stepPairCoverageString(possibleStepPairs, printAll, result);
    result += "\nUnique requirements: " + reqs.size();
    result += countString("Variable values", getValueCount(), possibleValues);
    result += countString("Unique coverage-values", getStateCount(), possibleCVs);
    result += countString("Unique coverage-value-pairs", getStatePairCount(), possibleCVPairs);

    return result;
  }

  private String stepPairCoverageString(Collection<String> possibleStepPairs, boolean printAll, String result) {
    result += "\nUnique step-pairs: " + stepPairs.size();
    if (possibleStepPairs != null && possibleStepPairs.size() > 0) {
      Collection<String> all = new LinkedHashSet<>();
      all.addAll(possibleStepPairs);
      all.removeAll(stepPairs);
      result += " (of " + possibleStepPairs.size() + ")";
      if (printAll || (all.size() > 0 && all.size() < 5)) {
        result += " missing:" + all;
      }
    }
    return result;
  }

  private String stepCoverageString(FSM fsm, String result) {
    result += "\nUnique steps: " + singles.size();
    if (fsm != null) {
      Collection<FSMTransition> fsmTransitions = fsm.getTransitions();
      int fsmMax = fsmTransitions.size();
      result += (" (of " + fsmMax + ")");
      if (fsmMax > singles.size()) {
        Collection<String> all = new LinkedHashSet<>();
        for (FSMTransition ft : fsmTransitions) {
          all.add(ft.getStringName());
        }
        all.removeAll(singles);
        result += " missing:" + all;
      }
    }
    return result;
  }

  private String countString(String name, int count, Map<String, Collection<String>> possibleValues) {
    String result = "\n" + name + ": " + count;
    if (possibleValues != null && possibleValues.size() > 0) {
      int possibleValueCount = 0;
      for (Collection<String> strings : possibleValues.values()) {
        possibleValueCount += strings.size();
      }
      result += " (of " + possibleValueCount + ")";
    }
    return result;
  }

  /**
   * Remove all coverage elements in the other set from this set.
   *
   * @param in The items to remove from this set.
   */
  public void removeAll(TestCoverage in) {
//    steps.removeAll(in.steps);
    singles.removeAll((in.singles));
    stepPairs.removeAll(in.stepPairs);
    reqs.removeAll(in.reqs);
    variables.removeAll(in.variables);
    for (String var : variableValues.keySet()) {
      Collection<String> yourValues = in.variableValues.get(var);
      if (yourValues == null) continue;
      Collection<String> myValues = variableValues.get(var);
      myValues.removeAll(yourValues);
    }
    for (String var : coverageValues.keySet()) {
      Collection<String> yourValues = in.coverageValues.get(var);
      if (yourValues == null) continue;
      Collection<String> myValues = coverageValues.get(var);
      myValues.removeAll(yourValues);
    }
    for (String var : coverageValuePairs.keySet()) {
      Collection<String> yourValues = in.coverageValuePairs.get(var);
      if (yourValues == null) continue;
      Collection<String> myValues = coverageValuePairs.get(var);
      myValues.removeAll(yourValues);
    }
  }

  public void coveredRequirement(String name) {
    reqs.add(name);
  }
}
