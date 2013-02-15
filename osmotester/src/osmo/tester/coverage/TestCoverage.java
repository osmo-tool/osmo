package osmo.tester.coverage;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.ModelVariable;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Represents test coverage for a set of test cases.
 * User to calculate coverage scores for test suite optimization.
 *
 * @author Teemu Kanstren
 */
public class TestCoverage {
  private static final Logger log = new Logger(TestCoverage.class);
  /** The list of transitions covered, in order, including duplicates. */
  private Collection<String> transitions = new ArrayList<>();
  /** The set of transition pairs covered. */
  private Collection<String> pairs = new LinkedHashSet<>();
  /** The unique set of transitions covered. */
  private Collection<String> singles = new LinkedHashSet<>();
  /** The set of covered requirements. */
  private Collection<String> reqs = new LinkedHashSet<>();
  /** Set of values covered for different model data variables. */
  private Map<String, Collection<String>> variables = new LinkedHashMap<>();
  /** Are we merging state? That is replacing old values in a state with new ones.. */
  private boolean mergeState = false;
  /** Custom calculators. */
  private Collection<CoverageCalculator> calculators= new LinkedHashSet<>();

  /**
   * Start with an empty set.
   */
  public TestCoverage() {
  }

  /**
   * Start with the given set.
   *
   * @param tests  Add coverage from all these tests.
   */
  public TestCoverage(Collection<TestCase> tests) {
    for (TestCase test : tests) {
      addTestCoverage(test);
    }
  }

  public void setMergeState(boolean mergeState) {
    this.mergeState = mergeState;
  }

  /**
   * Initialized with coverage for a single test case.
   *
   * @param test   The test covered.
   */
  public TestCoverage(TestCase test) {
    addTestCoverage(test);
  }
  
  public void addCalculator(CoverageCalculator calculator) {
    calculators.add(calculator);
  }

  /**
   * Gives all transitions in this test suite, including coverage number.
   * Coverage number tells how many times transition is taken in this test suite
   *
   * @return The transitions with coverage number
   */
  public Map<String, Integer> getTransitionCoverage() {
    Map<String, Integer> result = new HashMap<>();
    for (String transition : transitions) {
      Integer count = result.get(transition);
      if (count == null) {
        count = 0;
      }
      result.put(transition, count + 1);
    }
    return result;
  }

  /**
   * Add the coverage in the given test case.
   *
   * @param test The test to add.
   */
  public void addTestCoverage(TestCase test) {
    addTestCoverage(test, test.getAllTransitionNames().size());
  }

  /**
   * Add the coverage in the given test case up to a given number of steps in the test case.
   * Useful for calculating what would be the added coverage is different number of steps was added.
   * Especially when comparing different paths to each other.
   *
   * @param test      The test containing the steps to add.
   * @param stepCount The number of steps to take from the test.
   */
  public void addTestCoverage(TestCase test, int stepCount) {
    Collection<String> names = new ArrayList<>();

    int count = 0;
    Map<String, Map<String, ModelVariable>> stateMap = new LinkedHashMap<>();
    for (TestStep step : test.getSteps()) {
      names.add(step.getName());
      if (!step.isCoverageProcessed()) {
        calculate(step);
        step.setCoverageProcessed();
      }
      if (!mergeState) {
        addValues(step.getValues());
      } else {
        combineStateMap(stateMap, step);
      }
      reqs.addAll(step.getCoveredRequirements());
      count++;
      if (count == stepCount) {
        break;
      }
    }
    if (mergeState) {
      for (Map<String, ModelVariable> map : stateMap.values()) {
        addValues(map.values());
      }
    }
    addTransitions(names);
    log.debug("added coverage for " + stepCount + " steps in " + test);
  }

  private void combineStateMap(Map<String, Map<String, ModelVariable>> stateMap, TestStep step) {
    String state = step.getState();
    Map<String, ModelVariable> map = stateMap.get(state);
    if (map == null) {
      map = new LinkedHashMap<>();
      stateMap.put(state, map);
    }
    Collection<ModelVariable> values = step.getValues();
    for (ModelVariable value : values) {
      map.put(value.getName(), value);
    }
  }


  /**
   * Adds the values for the variables in the given test case to the covered set.
   *
   * @param toAdd The variables to add.
   */
  private void addValues(Collection<ModelVariable> toAdd) {
    for (ModelVariable variable : toAdd) {
      String name = variable.getName();
      Collection<String> values = variables.get(name);
      if (values == null) {
        values = new LinkedHashSet<>();
      }
      for (Object value : variable.getValues()) {
        values.add("" + value);
      }

      variables.put(name, values);
    }
  }

  /**
   * Add the set of given transitions to the sets representing various aspects of covered transitions.
   * That is transition list, unique single transitions, and transition pairs.
   *
   * @param names The names of transitions to add.
   */
  private void addTransitions(Collection<String> names) {
    transitions.addAll(names);
    singles.addAll(names);
    String previous = "init";
    for (String name : names) {
      pairs.add(previous + "-" + name);
      previous = name;
    }
  }

  /**
   * Runs the given set of coverage calculators on the given test step.
   * Stores the generated values as variable values for the step.
   *
   * @param step        The step to process.
   */
  private void calculate(TestStep step) {
    for (CoverageCalculator calculator : calculators) {
      ModelVariable temp = calculator.process(step);
      if (temp != null) {
        String name = temp.getName();
        Collection<Object> values = temp.getValues();
        for (Object value : values) {
          step.addVariableValue(name, value);
        }
      }
    }
  }

  public Collection<String> getTransitions() {
    return transitions;
  }

  public Collection<String> getPairs() {
    return pairs;
  }

  public Collection<String> getSingles() {
    return singles;
  }

  public Collection<String> getRequirements() {
    return reqs;
  }

  public Map<String, Collection<String>> getVariables() {
    return variables;
  }

  /**
   * Creates a clone of this coverage set.
   * The clone can then be modified without affecting the original set (no reference to same internal lists etc.).
   *
   * @return The clone object.
   */
  public TestCoverage cloneMe() {
    TestCoverage clone = new TestCoverage();
    clone.pairs.addAll(pairs);
    clone.transitions.addAll(transitions);
    clone.reqs.addAll(reqs);
    clone.singles.addAll(singles);
    for (String key : variables.keySet()) {
      Collection<String> values = new LinkedHashSet<>();
      values.addAll(variables.get(key));
      clone.variables.put(key, values);
    }
    return clone;
  }

  @Override
  public String toString() {
    return "TestCoverage{" +
            "transitions=" + transitions +
            ", pairs=" + pairs +
            ", singles=" + singles +
            ", requirements=" + reqs +
            ", variables=" + variables +
            '}';
  }
}
