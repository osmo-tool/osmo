package osmo.tester.reporting.coverage;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;

import java.util.*;

/**
 * Coverage values for a single test case (as opposed to suite in {@link osmo.tester.coverage.TestCoverage}.
 * Used in reporting for coverage matrix. Number of times different elements were covered or the values for
 * variables.
 *
 * @author Teemu Kanstren
 */
public class SingleTestCoverage {
  /** Test name. */
  private final String name;
  /** Key=requirement name, value=times covered in this test. */
  private final Map<String, Integer> reqCount = new LinkedHashMap<>();
  /** Key=transition name, value=times covered in this test. */
  private final Map<String, Integer> stepCount = new LinkedHashMap<>();
  /** Key=transition pair name ("T1->T2"), value=times covered in this test. */
  private final Map<String, Integer> pairCount = new LinkedHashMap<>();
  /** Key=variable name, value=values covered in this test. */
  private final Map<String, Collection<String>> variableValues = new LinkedHashMap<>();
  private final CoverageMetric parent;

  /**
   * Counts and collects the coverage of the given test.
   *
   * @param tc The test for which to get the coverage.
   * @param parent The metric collector aggregating test coverages.
   */
  public SingleTestCoverage(TestCase tc, CoverageMetric parent) {
    this.name = tc.getName();
    this.parent = parent;
    countRequirements(tc);
    countSteps(tc);
    collectVariableValues(tc);
  }

  /**
   * Count the number of times different requirements have been covered.
   *
   * @param tc The test case to count the requirements from..
   */
  private void countRequirements(TestCase tc) {
    Collection<String> covered = tc.getCoverage().getRequirements();
    for (String req : covered) {
      incrementCountFor(reqCount, req);
    }
  }

  public List<RequirementCount> allReqCounts() {
    List<RequirementCount> counts = new ArrayList<>();
    List<String> reqNames = parent.getRequirements();
    for (String reqName : reqNames) {
      counts.add(new RequirementCount(reqName, reqCount(reqName)));
    }
    return counts;
  }

  /**
   * Count the number of times each step and step pair has been covered in a given test case.
   * If coverage is 0, nothing is given for that step/pair.
   *
   * @param tc The test case for which to get the coverage.
   */
  private void countSteps(TestCase tc) {
    Collection<String> names = tc.getAllStepNames();
    String previous = FSM.START_STEP_NAME;
    for (String name : names) {
      incrementCountFor(stepCount, name);
      String pair = previous + "->" + name;
      incrementCountFor(pairCount, pair);
      previous = name;
    }
  }

  public List<RequirementCount> allStepCounts() {
    List<RequirementCount> counts = new ArrayList<>();
    List<String> stepNames = parent.getSteps();
    for (String stepName : stepNames) {
      counts.add(new RequirementCount(stepName, stepCount(stepName)));
    }
    return counts;
  }

  /**
   * Increment the count for the given key in the given map.
   *
   * @param map  Where to find the value.
   * @param name The name of the key for the value.
   */
  private void incrementCountFor(Map<String, Integer> map, String name) {
    Integer count = map.get(name);
    if (count == null) {
      count = 0;
    }
    count++;
    map.put(name, count);
  }

  /**
   * Captures all values that different model variables have received in a given test case.
   *
   * @param tc To get the values from.
   */
  private void collectVariableValues(TestCase tc) {
    Map<String, Collection<String>> variables = tc.getCoverage().getVariableValues();
    for (String var : variables.keySet()) {
      Collection<String> values = variableValues.get(var);
      if (values == null) {
        values = new LinkedHashSet<>();
        variableValues.put(var, values);
      }
      values.addAll(variables.get(var));
    }
  }

  public String getName() {
    return name;
  }

  /**
   * Gives the coverage count for the given key.
   *
   * @param map  Where to get the coverage count from.
   * @param name The key name to get the count for.
   * @return The coverage count for the given key.
   */
  private int countFor(Map<String, Integer> map, String name) {
    Integer count = map.get(name);
    if (count == null) {
      count = 0;
    }
    return count;
  }

  /**
   * Used in reporting via Velocity templates.
   * Gives the number of times the given requirement was covered in this test case.
   *
   * @param requirement The name of the requirement to get the count for.
   * @return The times covered in this test case.
   */
  public int reqCount(String requirement) {
    return countFor(reqCount, requirement);
  }

  /**
   * Used in reporting via Velocity templates.
   * Gives the number of times the given step was covered in this test case.
   *
   * @param step The name of the step to get the count for.
   * @return The times covered in this test case.
   */
  public int stepCount(String step) {
    return countFor(stepCount, step);
  }

  /**
   * Used in reporting via Velocity templates.
   * Gives the number of times the given step pair was covered in this test case.
   *
   * @param pair The name of the step pair to get the count for.
   * @return The times covered in this test case.
   */
  public int pairCount(String pair) {
    return countFor(pairCount, pair);
  }

  public List<RequirementCount> allPairCounts() {
    List<RequirementCount> counts = new ArrayList<>();
    List<String> pairNames = parent.getStepPairs();
    for (String pairName : pairNames) {
      counts.add(new RequirementCount(pairName, pairCount(pairName)));
    }
    return counts;
  }

  /**
   * Used in reporting via Velocity templates.
   * Gives the values that were covered for the given variable in this test case.
   *
   * @param variable The name of the variable to get the values for.
   * @return The values covered in this test case.
   */
  public Collection<String> variableCoverage(String variable) {
    Collection<String> values = variableValues.get(variable);
    if (values != null) return values;
    return Collections.EMPTY_LIST;
  }

  public List<VariableValues> allVariables() {
    List<VariableValues> counts = new ArrayList<>();
    List<String> variableNames = parent.getVariables();
    for (String varName : variableNames) {
      counts.add(new VariableValues(varName, variableCoverage(varName)));
    }
    return counts;
  }

  public Collection<String> variableNames() {
    return variableValues.keySet();
  }
}
