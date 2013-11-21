package osmo.tester.coverage;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.VariableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
  /** The list of test steps covered, in order, including duplicates. */
  private Collection<String> steps = new ArrayList<>();
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
  private String previousStep = FSM.START_STEP_NAME;
  private Map<String, String> previousCoverageValues = new LinkedHashMap<>();

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

//  /**
//   * Start with the given set.
//   *
//   * @param tests  Add coverage from all these tests.
//   */
//  public TestCoverage(Collection<TestCase> tests) {
//    for (TestCase test : tests) {
//      addTestCoverage(test);
//    }
//  }
//
//  /**
//   * Initialized with coverage for a single test case.
//   *
//   * @param test   The test covered.
//   */
//  public TestCoverage(TestCase test) {
//    addTestCoverage(test);
//  }

  /**
   * Gives names of all steps and number of times each is covered in whatever this object is tracking.
   *
   * @return The test steps with coverage number.
   */
  public Map<String, Integer> getStepCoverage() {
    Map<String, Integer> result = new HashMap<>();
    for (String step : steps) {
      Integer count = result.get(step);
      if (count == null) {
        count = 0;
      }
      result.put(step, count + 1);
    }
    return result;
  }

//  /**
//   * Add the coverage in the given test case.
//   *
//   * @param test The test to add.
//   */
//  public synchronized void addTestCoverage(TestCase test) {
//    addTestCoverage(test, test.getAllStepNames().size());
//  }
//
//  /**
//   * Adds coverage for all the given tests.
//   * 
//   * @param tests To add.
//   * @return Self reference.
//   */
//  public TestCoverage addAll(Collection<TestCase> tests) {
//    for (TestCase test : tests) {
//      addTestCoverage(test);
//    }
//    return this;
//  }

//  /**
//   * Add the coverage in the given test case up to a given number of steps in the test case.
//   * Useful for calculating what would be the added coverage is different number of steps was added.
//   * Especially when comparing different explored paths to each other.
//   *
//   * @param test      The test containing the steps to add.
//   * @param stepCount The number of steps to take from the test.
//   */
//  public synchronized void addTestCoverage(TestCase test, int stepCount) {
//    Collection<String> names = new ArrayList<>();
//
//    int count = 0;
//    for (TestCaseStep step : test.getSteps()) {
//      String name = step.getName();
//      names.add(name);
//      addStates(step);
//      addValues(step);
//      reqs.addAll(step.getCoveredRequirements());
//      count++;
//      if (count == stepCount) {
//        break;
//      }
//    }
//    addSteps(names);
//    log.debug("added coverage for " + stepCount + " steps in " + test);
//  }
//
//  /**
//   * Adds the values for the variables in the given test case to the covered set.
//   *
//   * @param step This is where we take the steps from.
//   */
//  private synchronized void addValues(TestCaseStep step) {
//    Collection<ModelVariable> toAdd = step.getValues();
//    for (ModelVariable variable : toAdd) {
//      String name = variable.getName();
//      variables.add(name);
//      Collection<String> values = this.values.get(name);
//      if (values == null) {
//        values = new LinkedHashSet<>();
//      }
//      for (Object value : variable.getValues()) {
//        values.add("" + value);
//      }
//      this.values.put(name, values);
//    }
//  }

//  /**
//   * Adds coverage values from the given step, including pairs.
//   * For the format of pair value data, see {@link osmo.tester.generator.MainGenerator} and method
//   * storeUserCoverageValues() or whatever it might be when you read this..
//   * 
//   * @param step Add coverage values from here.
//   */
//  private synchronized void addStates(TestCaseStep step) {
//    Collection<ModelVariable> toAdd = step.getStatesList();
//    for (ModelVariable variable : toAdd) {
//      String name = variable.getName();
//      Collection<String> values = this.coverageValues.get(name);
//      if (values == null) {
//        values = new LinkedHashSet<>();
//        this.coverageValues.put(name, values);
//      }
//      for (Object value : variable.getValues()) {
//        values.add("" + value);
//      }
//    }
//
//    //what value if any observed for pairs? e.g., "bob->alice"
//    toAdd = step.getStatePairsList();
//    for (ModelVariable variable : toAdd) {
//      String name = variable.getName();
//      Collection<String> values = this.coverageValuePairs.get(name);
//      if (values == null) {
//        values = new LinkedHashSet<>();
//        this.coverageValuePairs.put(name, values);
//      }
//      for (Object value : variable.getValues()) {
//        values.add("" + value);
//      }
//    }
//  }
//
//  /**
//   * Add the set of given test steps to the sets representing various aspects of covered test steps.
//   * That is all steps list, unique steps, and step pairs.
//   *
//   * @param names The names of steps to add.
//   */
//  private synchronized void addSteps(Collection<String> names) {
//    steps.addAll(names);
//    singles.addAll(names);
//    String previous = FSM.START_STEP_NAME;
//    for (String name : names) {
//      stepPairs.add(previous + "->" + name);
//      previous = name;
//    }
//  }

  /** @param name The name of step to add. */
  public synchronized void addStep(String name) {
    steps.add(name);
    singles.add(name);
    stepPairs.add(previousStep + "->" + name);
    previousStep = name;
  }

  public Collection<String> getSteps() {
    return steps;
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
//    clone.stepPairs.addAll(stepPairs);
//    clone.steps.addAll(steps);
//    clone.reqs.addAll(reqs);
//    clone.singles.addAll(singles);
//    clone.variables.addAll(variables);
//    for (String key : variableValues.keySet()) {
//      Collection<String> values = new LinkedHashSet<>();
//      values.addAll(this.variableValues.get(key));
//      clone.variableValues.put(key, values);
//    }
//    for (String key : coverageValues.keySet()) {
//      Collection<String> values = new LinkedHashSet<>();
//      values.addAll(this.coverageValues.get(key));
//      clone.coverageValues.put(key, values);
//    }
//    for (String key : coverageValuePairs.keySet()) {
//      Collection<String> values = new LinkedHashSet<>();
//      values.addAll(this.coverageValuePairs.get(key));
//      clone.coverageValuePairs.put(key, values);
//    }
    return clone;
  }

  public synchronized void addCoverage(TestCoverage from) {
    addCoverage(from, this);
  }

  private static void addCoverage(TestCoverage from, TestCoverage to) {
    to.stepPairs.addAll(from.stepPairs);
    to.steps.addAll(from.steps);
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
            "steps=" + steps +
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
    result += "Total steps: " + steps.size();
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
      Collection<String> all = new HashSet<>();
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
        Collection<String> all = new HashSet<>();
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
    steps.removeAll(in.steps);
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

  /**
   * Stores the general step state. General state refers to the state in the model variables that the generator sees.
   * Another state is the user defined state, which is queried from specifically annotated methods with
   * {@link osmo.tester.annotation.CoverageValue}.
   *
   * @param fsm We grab references to the state objects from here.
   */
  public void storeGeneralState(FSM fsm) {
    Collection<VariableField> variables = fsm.getModelVariables();
    for (VariableField variable : variables) {
      String name = variable.getName();
      Object value = variable.getValue();
      if (!variable.isSearchableInput()) {
        addVariableValue(name, "" + value);
      }
    }
  }
}
