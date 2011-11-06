package osmo.tester.optimizer.online;

/** @author Teemu Kanstren */
public class SearchConfiguration {
  private int lengthWeight = 10;
  private int variableWeight = 10;
  private int valueWeight = 1;
  private int pairsWeight = 10;
  private int transitionWeight = 10;
  private int iterations = 100;
  private int numberOfCandidates = 1000;
  private int populationSize = 50;

  public SearchConfiguration() {
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

  public int getIterations() {
    return iterations;
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
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
}
