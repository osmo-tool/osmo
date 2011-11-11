package osmo.tester.optimizer.online;

import osmo.tester.OSMOTester;
import osmo.tester.generator.MainGenerator;

/** @author Teemu Kanstren */
public class SearchConfiguration {
  private int lengthWeight = 10;
  private int variableWeight = 10;
  private int valueWeight = 1;
  private int pairsWeight = 10;
  private int transitionWeight = 10;
  private int requirementWeight = 10;
  private int numberOfCandidates = 1000;
  private int populationSize = 50;
  private SearchEndCondition endCondition = new PeakEndCondition(50);
  private final MainGenerator generator;

  public SearchConfiguration(MainGenerator generator) {
    this.generator = generator;
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
