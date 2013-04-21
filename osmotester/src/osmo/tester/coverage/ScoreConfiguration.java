package osmo.tester.coverage;

import osmo.common.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.VariableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * A configuration for scoring test coverage.
 *
 * @author Teemu Kanstren
 */
public class ScoreConfiguration {
  private static final Logger log = new Logger(ScoreConfiguration.class);
  /** Weight for length (number of non-unique transitions), used in fitness calculation. */
  protected int lengthWeight = 10;
  /** Weight for number of variables, used in fitness calculation. */
  protected int variableCountWeight = 10;
  /** Weight for number of unique values if no specific one defined for a variable, used in fitness calculation. */
  protected int defaultValueWeight = 1;
  /** Weights for specific variables (each unique value scores this much). */
  protected Map<String, Integer> valueWeights = new LinkedHashMap<>();
  /** Weight for number of unique transition pairs (subsequent transitions), used in fitness calculation. */
  protected int stepPairWeight = 10;
  /** Weight for number of unique steps, used in fitness calculation. */
  protected int stepWeight = 10;
  /** Weight for number of covered requirements, used in fitness calculation. */
  protected int requirementWeight = 10;
  /** Weight for covered user defined states. */
  private int stateWeight = 50;
  /** Weight for covered pairs of user defined states (transitions between them). */
  private int statePairWeight = 40;
  /** The combination variable calculators that combine several existing variables to new ones for coverage. */
  protected Collection<CombinationCoverage> combinations = new ArrayList<>();
  /** Names of variables that should not be validated, e.g. custom user variables. */
  protected Collection<String> ignoreList = new LinkedHashSet<>();

  /**
   * Validates the coverage criteria defined in this configuration against the given model.
   * Meaning that all the defined input variables should be found in the given model (unless added to the ignore list).
   *
   * @param fsm The model to check against.
   */
  public void validate(FSM fsm) {
    log.debug("validating against:" + fsm);
    Collection<VariableField> coverageVariables = fsm.getCoverageVariables();
    Collection<String> variableNames = new ArrayList<>();
    for (VariableField field : coverageVariables) {
      variableNames.add(field.getName());
    }
    log.debug("FSM variables for coverage:" + variableNames);
    Collection<String> notFound = new HashSet<>();
    for (String name : valueWeights.keySet()) {
      if (!variableNames.contains(name) && !name.contains("&") && !ignoreList.contains(name)) {
        notFound.add(name);
      }
    }
    for (CoverageCalculator calculator : getAllCalculators()) {
      Collection<String> inputNames = calculator.getInputNames();
      for (String name : inputNames) {
        if (!variableNames.contains(name) && !name.contains("&") && !ignoreList.contains(name)) {
          notFound.add(name);
        }
      }
    }
    if (notFound.size() == 0) {
      return;
    }
    List<String> sortMe = new ArrayList<>();
    sortMe.addAll(notFound);
    Collections.sort(sortMe);
    log.debug("Some required variables not found:" + sortMe);
    throw new IllegalArgumentException("Following coverage variables not found in the model:" + sortMe);
  }

  public int getLengthWeight() {
    return lengthWeight;
  }

  public void setLengthWeight(int lengthWeight) {
    this.lengthWeight = lengthWeight;
  }

  public int getVariableCountWeight() {
    return variableCountWeight;
  }

  public void setVariableCountWeight(int weight) {
    this.variableCountWeight = weight;
  }

  /**
   * Provides the weight to give for unique values of the variable with the given name.
   *
   * @param name The name of the variable.
   * @return Weight for variable, or default weight if no specific value defined.
   */
  public int getVariableWeight(String name) {
    Integer weight = valueWeights.get(name);
    if (weight == null) {
      weight = defaultValueWeight;
    }
    return weight;
  }

  public int getStateWeight() {
    return stateWeight;
  }

  public void setStateWeight(int stateWeight) {
    this.stateWeight = stateWeight;
  }

  public int getStepPairWeight() {
    return stepPairWeight;
  }

  public void setStepPairWeight(int pairsWeight) {
    this.stepPairWeight = pairsWeight;
  }

  public int getStatePairWeight() {
    return statePairWeight;
  }

  public void setStatePairWeight(int statePairWeight) {
    this.statePairWeight = statePairWeight;
  }

  public int getStepWeight() {
    return stepWeight;
  }

  public void setStepWeight(int stepWeight) {
    this.stepWeight = stepWeight;
  }

  public void setRequirementWeight(int requirementWeight) {
    this.requirementWeight = requirementWeight;
  }

  public int getRequirementWeight() {
    return requirementWeight;
  }

  public Map<String, Integer> getValueWeights() {
    return valueWeights;
  }

  public void setDefaultValueWeight(int weight) {
    defaultValueWeight = weight;
  }


  public void setVariableWeight(String name, int weight) {
    valueWeights.put(name, weight);
  }

  /**
   * Disables checking for a specific variable names, meaning they will be accepted even if not found in the FSM model.
   *
   * @param names The names to ignore.
   */
  public void disableCheckingFor(String... names) {
    Collections.addAll(ignoreList, names);
  }

  public Collection<CombinationCoverage> getCombinations() {
    return combinations;
  }

  /** @return The different types of coverage calculators: ranges, combinations, ... */
  protected Collection<CoverageCalculator> getAllCalculators() {
    Collection<CoverageCalculator> result = new ArrayList<>();
    result.addAll(combinations);
    return result;
  }

  /**
   * Adds a new coverage variable as a combination of the values of all given input variables.
   *
   * @param weight The weight of the new combination.
   * @param names  The names of all input variables for which the values need to be taken.
   */
  public void addCombination(int weight, String... names) {
    List<String> nameList = new ArrayList<>();
    Collections.addAll(nameList, names);
    Collections.sort(nameList);
    String name = combine(nameList);
    valueWeights.put(name, weight);
    CombinationCoverage combo = new CombinationCoverage(names);
    combinations.add(combo);
    log.debug("Added coverage combination:" + combo);
  }

  /**
   * Combines a list of given input variable names or values into a format used internally to represent their
   * combinations.
   * In practice they are concatenated with "&" in between.
   *
   * @param values The stuff we are combining.
   * @return The combined string.
   */
  public static String combine(List<? extends Object> values) {
    String combination = "";
    for (Object value : values) {
      combination += value + "&";
    }
    combination = combination.substring(0, combination.length() - 1);
    return combination;
  }

  public void bind(TestCoverage tc) {
    Collection<CoverageCalculator> calculators = getAllCalculators();
    for (CoverageCalculator calculator : calculators) {
      tc.addCalculator(calculator);
    }
  }

  @Override
  public String toString() {
    return "ScoreConfiguration{" +
            "lengthWeight=" + lengthWeight +
            ", variableCountWeight=" + variableCountWeight +
            ", defaultValueWeight=" + defaultValueWeight +
            ", stepPairWeight=" + stepPairWeight +
            ", stepWeight=" + stepWeight +
            ", requirementWeight=" + requirementWeight +
            ", stateWeight=" + stateWeight +
            ", statePairWeight=" + statePairWeight +
            '}';
  }
}
