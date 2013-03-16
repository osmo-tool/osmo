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
  /**
   * Weights for specific variables and their values.
   * Each unique value for a variable is given the weight defined for that variable in this mapping.
   */
  protected Map<String, Integer> valueWeights = new LinkedHashMap<>();
  /** Weight for number of unique transition pairs (subsequent transitions), used in fitness calculation. */
  protected int pairsWeight = 10;
  /** Weight for number of unique transitions, used in fitness calculation. */
  protected int transitionWeight = 10;
  /** Weight for number of covered requirements, used in fitness calculation. */
  protected int requirementWeight = 10;
  /** Weight for covered user defined states. */
  private int stateWeight = 50;
  /** The combination variable calculators that combine several existing variables to new ones for coverage. */
  protected Collection<CombinationCoverage> combinations = new ArrayList<>();
  /** The set of value ranges defined. */
  private Map<String, RangeCategory> ranges = new LinkedHashMap<>();
  /**
   * A list of variable names that should be ignored when validating if coverage criteria are possible to compute.
   * Can be useful, for example, if the user adds custom values directly in the model that have no variables.
   */
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

  public int getPairsWeight() {
    return pairsWeight;
  }

  public void setPairsWeight(int pairsWeight) {
    this.pairsWeight = pairsWeight;
  }

  public int getTransitionWeight() {
    return transitionWeight;
  }

  public void setStepWeight(int transitionWeight) {
    this.transitionWeight = transitionWeight;
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
    result.addAll(ranges.values());
    result.addAll(combinations);
    return result;
  }

  /**
   * Creates a new integer range for the given variable, with the given definitions.
   * If the variable already has existing range definitions, this will be added on top of those.
   * Thus it does not impact the existing ones in any way.
   *
   * @param variableName The name of the input variable.
   * @param categoryName The name of the range category, for example, "one" for 1-1="one"
   * @param min          The minimum value in the range.
   * @param max          The maximum value in the range.
   */
  public void addRange(String variableName, String categoryName, int min, int max) {
    String rangeName = variableName + "-range";
    RangeCategory range = ranges.get(rangeName);
    if (range == null) {
      range = new RangeCategory(variableName, rangeName);
      ranges.put(rangeName, range);
    }
    IntegerRange added = range.addCategory(min, max, categoryName);
    ignoreList.add(rangeName);
    log.debug("New range added for variable " + variableName + ":" + added);
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
   * Convenience method for creating a specific integer range category with the given parameters.
   * This produces three categories: 0-0 = "zero", 1-1 = "one", 1-N = "many".
   *
   * @param inputName The name of the input variable to create the range category for.
   * @param weight    The coverage weight of the created range.
   */
  public void addZeroOneManyRange(String inputName, int weight) {
    addRange(inputName, "zero", 0, 0);
    addRange(inputName, "one", 1, 1);
    addRange(inputName, "many", 2, Integer.MAX_VALUE);
    setRangeWeight(inputName, weight);
    log.debug("added zero-one-many range:" + inputName + "=" + weight);
  }

  public Collection<RangeCategory> getRanges() {
    return ranges.values();
  }

  /**
   * Convenience method for creating a specific integer range category with the given parameters.
   * This produces three categories: 1-1 = "one", 2-2 = "two", 1-N = "many".
   *
   * @param inputName The name of the input variable to create the range category for.
   * @param weight    The coverage weight of the created range.
   */
  public void addOneTwoManyRange(String inputName, int weight) {
    addRange(inputName, "one", 1, 1);
    addRange(inputName, "two", 2, 2);
    addRange(inputName, "many", 3, Integer.MAX_VALUE);
    setRangeWeight(inputName, weight);
    log.debug("added one-two-many range:" + inputName + "=" + weight);
  }

  public void setRangeWeight(String inputName, int weight) {
    String rangeName = inputName + "-range";
    valueWeights.put(rangeName, weight);
    ignoreList.add(rangeName);
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
    return "ScoreConfiguration{\n" +
            "lengthWeight=" + lengthWeight +
            ",\n variableCountWeight=" + variableCountWeight +
            ",\n defaultValueWeight=" + defaultValueWeight +
            ",\n valueWeights=" + valueWeights +
            ",\n pairsWeight=" + pairsWeight +
            ",\n transitionWeight=" + transitionWeight +
            ",\n requirementWeight=" + requirementWeight +
            ",\n ignoreList=" + ignoreList +
            '}';
  }

}
