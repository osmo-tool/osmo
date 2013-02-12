package osmo.tester.suiteoptimizer.coverage;

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
  /** Defines how the coverage score is calculated. */
  private final ScoreConfiguration config;

  /**
   * Start with an empty set.
   *
   * @param config Used for coverage value calculations.
   */
  public TestCoverage(ScoreConfiguration config) {
    this.config = config;
  }

  /**
   * Start with the given set.
   *
   * @param tests  Add coverage from all these tests.
   * @param config Used for coverage value calculations.
   */
  public TestCoverage(Collection<TestCase> tests, ScoreConfiguration config) {
    this.config = config;
    for (TestCase test : tests) {
      addTestCoverage(test);
    }
  }

  /**
   * Initialized with coverage for a single test case.
   *
   * @param test   The test covered.
   * @param config Used for coverage value calculations.
   */
  public TestCoverage(TestCase test, ScoreConfiguration config) {
    this.config = config;
    addTestCoverage(test);
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
      //these calculations add values to the step, which are stored after on the addValues(step.getValues()) call
      //we only want to add them once so we store with each step the information that we already processed it
      //(to avoid duplicate values)
      if (!step.isCoverageProcessed()) {
        calculate(step, config.getAllCalculators());
        step.setCoverageProcessed();
      }
      if (!config.isStateMerging()) {
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
    if (config.isStateMerging()) {
      for (Map<String, ModelVariable> map : stateMap.values()) {
        addValues(map.values());
      }
    }
//    addValues(stateMap);
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
   * @param calculators The calculators to run.
   */
  private void calculate(TestStep step, Collection<? extends CoverageCalculator> calculators) {
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
   * Calculates the coverage score for the data represented in this coverage object with the given score configuration.
   *
   * @return The score for the set described in this object.
   */
  public int calculateFitness() {
    int fitness = transitions.size() * config.getLengthWeight();
    fitness += singles.size() * config.getTransitionWeight();
    fitness += pairs.size() * config.getPairsWeight();
    fitness += variables.size() * config.getVariableCountWeight();
    for (String name : variables.keySet()) {
      Collection<String> values = variables.get(name);
      fitness += values.size() * config.getVariableWeight(name);
    }
    fitness += reqs.size() * config.getRequirementWeight();
    log.debug("calculated fitness:" + fitness);
    return fitness;
  }

  public int addedFitnessFor(TestCase test) {
    return addedFitnessFor(test, test.getAllTransitionNames().size());
  }

  /**
   * Calculates how much the coverage score would raise if the given test case was added to this set.
   * Does not add anything to this set, so after this the set is the same as before.
   *
   * @param test The test to check added coverage for.
   * @return The new coverage score.
   */
  public int addedFitnessFor(TestCase test, int steps) {
    TestCoverage tc = cloneMe();
    tc.addTestCoverage(test, steps);
    int oldScore = calculateFitness();
    int newScore = tc.calculateFitness();
    int added = newScore - oldScore;
    log.debug("added fitness:" + added);
    return added;
  }

  /**
   * Creates a clone of this coverage set.
   * The clone can then be modified without affecting the original set (no reference to same internal lists etc.).
   *
   * @return The clone object.
   */
  public TestCoverage cloneMe() {
    TestCoverage clone = new TestCoverage(config);
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
