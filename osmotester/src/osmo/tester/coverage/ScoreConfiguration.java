package osmo.tester.coverage;

import osmo.common.log.Logger;
import osmo.tester.model.CoverageMethod;
import osmo.tester.model.FSM;
import osmo.tester.model.VariableField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * A configuration for scoring test coverage.
 * Give weights to different elements of test coverage, and these are used for coverage calculation.
 * Note that also weight of zero and negative weights are possible.
 * With a weight of zero, that coverage criteria is ignored.
 * 
 * With a negative weight, the generator prefers to have as little as possible of that value.
 * For example, with a negative weight for length the generator will prefer shorter test cases.
 * Something to note in such a case is the optimizers in the OSMO Tester toolset look for anything that
 * adds a given amount of coverage score to the set. Thus, using negative length weights can lead the
 * tool to ignore some potential gain if it is only achieved after a very long sequence of steps.
 *
 * @author Teemu Kanstren
 */
public class ScoreConfiguration {
  private static final Logger log = new Logger(ScoreConfiguration.class);
  /** Weight for length (number of steps). Defaults to -1 to favour shorter tests. */
  protected int lengthWeight = -1;
  /** Weight for number of coverage variables covered. One variable counts once regardless of number of values. */
  protected int variableCountWeight = 10;
  /** Weight for unique values for coverage variables, if no specific one is defined for a variable. */
  protected int defaultValueWeight = 1;
  /** Weights for specific coverage variables (each unique value for that variable scores this much). */
  protected Map<String, Integer> valueWeights = new LinkedHashMap<>();
  /** Weight for number of unique step-pairs (two steps in a sequence). */
  protected int stepPairWeight = 30;
  /** Weight for number of unique steps covered. */
  protected int stepWeight = 20;
  /** Weight for number of covered requirements. */
  protected int requirementWeight = 10;
  /** Names of variable names that should not be validated, e.g. custom user variables. */
  protected Collection<String> ignoreList = new LinkedHashSet<>();
  /** Weight for custom state, the ones tagged with @CoverageValue. */
  private int stateWeight = 50;
  /** Weight for pairs of custom state, the ones tagged with @CoverageValue. */
  private int statePairWeight = 40;

  /**
   * Validates the coverage criteria defined in this configuration against the given model.
   * Meaning that all the defined input variables should be found in the given model (unless added to the ignore list).
   *
   * @param fsm The model to check against.
   */
  public void validate(FSM fsm) {
    log.debug("validating against:" + fsm);
    Collection<VariableField> coverageVariables = fsm.getModelVariables();
    Collection<String> variableNames = new ArrayList<>();
    for (VariableField field : coverageVariables) {
      variableNames.add(field.getName());
    }
    Collection<CoverageMethod> coverageMethods = fsm.getCoverageMethods();
    for (CoverageMethod method : coverageMethods) {
      variableNames.add(method.getVariableName());
      variableNames.add(method.getPairName());
    }
    log.debug("FSM variables for coverage:" + variableNames);
    //use a hashset to avoid duplicates
    Collection<String> notFound = new LinkedHashSet<>();
    for (String name : valueWeights.keySet()) {
      if (!variableNames.contains(name) && !ignoreList.contains(name)) {
        //a variable defined for score calculation does not exist in the code
        notFound.add(name);
      }
    }
    if (notFound.size() == 0) {
      return;
    }
    //recreate a list that can be sorted
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

  public int getStepPairWeight() {
    return stepPairWeight;
  }

  public void setStepPairWeight(int pairsWeight) {
    this.stepPairWeight = pairsWeight;
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

  public int getDefaultValueWeight() {
    return defaultValueWeight;
  }

  public void setVariableWeight(String name, int weight) {
    valueWeights.put(name, weight);
  }

  /**
   * Disables checking for a specific variable names, meaning they will be accepted even if not found in the FSM model.
   *
   * @param names The names to ignore.
   */
  public void ignore(String... names) {
    Collections.addAll(ignoreList, names);
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
            '}';
  }

  public int getStateWeight() {
    return stateWeight;
  }

  public void setStateWeight(int stateWeight) {
    this.stateWeight = stateWeight;
  }

  public int getStatePairWeight() {
    return statePairWeight;
  }

  public void setStatePairWeight(int statePairWeight) {
    this.statePairWeight = statePairWeight;
  }
}
