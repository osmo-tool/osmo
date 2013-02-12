package osmo.tester.suiteoptimizer.coverage;

import osmo.common.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.VariableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
  /**
   * A list of variable names that should be ignored when validating if coverage criteria are possible to compute.
   * Can be useful, for example, if the user adds custom values directly in the model that have no variables.
   */
  protected Collection<String> ignoreList = new LinkedHashSet<>();
  /** If true, we merge values for the same state so only the last value for a state is used for coverage. */
  private boolean stateMerging = false;

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
    throw new IllegalArgumentException("Following coverage variables not found in the model:" + notFound);
  }

  /** @return The different types of coverage calculators: ranges, combinations, ... */
  protected Collection<CoverageCalculator> getAllCalculators() {
    Collection<CoverageCalculator> result = new ArrayList<>();
    return result;
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

  public boolean isStateMerging() {
    return stateMerging;
  }

  public void setStateMerging(boolean stateMerging) {
    this.stateMerging = stateMerging;
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
