package osmo.tester.optimizer;

import osmo.tester.generator.MainGenerator;
import osmo.tester.optimizer.online.PeakEndCondition;
import osmo.tester.optimizer.online.SearchEndCondition;

/**
 * Configuration for suite optimization.
 *
 * @author Teemu Kanstren
 */
public class SearchConfiguration {
  /** Weight for length (number of non-unique transitions), used in fitness calculation. */
  private int lengthWeight = 10;
  /** Weight for number of variables, used in fitness calculation. */
  private int variableWeight = 10;
  /** Weight for number of unique values, used in fitness calculation. */
  private int valueWeight = 1;
  /** Weight for number of unique transition pairs (subsequent transitions), used in fitness calculation. */
  private int pairsWeight = 10;
  /** Weight for number of unique transitions, used in fitness calculation. */
  private int transitionWeight = 10;
  /** Weight for number of covered requirements, used in fitness calculation. */
  private int requirementWeight = 10;
  /** Number of candidate test suites to generate for optimization (also generates source set). */
  private int numberOfCandidates = 1000;
  /** Size of requested optimized candidate (test suite). */
  private int populationSize = 50;
  /** Seed value used to initialize random generator for optimizer. */
  private long seed = System.currentTimeMillis();
  /** The end condition if using {@link osmo.tester.optimizer.online.SearchingOptimizer}. */
  private SearchEndCondition endCondition = new PeakEndCondition(50);
  /** The generator used to generate tests. */
  private final MainGenerator generator;

  public SearchConfiguration(MainGenerator generator) {
    this.generator = generator;
  }

  public long getSeed() {
    return seed;
  }

  public void setSeed(long seed) {
    this.seed = seed;
  }

  public SearchEndCondition getEndCondition() {
    return endCondition;
  }

  public void setEndCondition(SearchEndCondition endCondition) {
    this.endCondition = endCondition;
  }

  public int getLengthWeight() {
    return lengthWeight;
  }

  public void setLengthWeight(int lengthWeight) {
    this.lengthWeight = lengthWeight;
  }

  public int getVariableWeight() {
    return variableWeight;
  }

  public void setVariableWeight(int variableWeight) {
    this.variableWeight = variableWeight;
  }

  public int getValueWeight() {
    return valueWeight;
  }

  public void setValueWeight(int valueWeight) {
    this.valueWeight = valueWeight;
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

  public void setTransitionWeight(int transitionWeight) {
    this.transitionWeight = transitionWeight;
  }

  public int getNumberOfCandidates() {
    return numberOfCandidates;
  }

  public void setNumberOfCandidates(int numberOfCandidates) {
    this.numberOfCandidates = numberOfCandidates;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public MainGenerator getGenerator() {
    return generator;
  }

  public void setRequirementWeight(int requirementWeight) {
    this.requirementWeight = requirementWeight;
  }

  public int getRequirementWeight() {
    return requirementWeight;
  }
}
