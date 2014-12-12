package osmo.tester.gui.jfx.configurationtab.algorithms;

import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.algorithm.WeightedBalancingAlgorithm;

/**
 * @author Teemu Kanstren
 */
public class WeightedBalancingDescription implements AlgorithmDescription {
  @Override
  public FSMTraversalAlgorithm getAlgorithm() {
    return new WeightedBalancingAlgorithm();
  }

  @Override
  public String toString() {
    return "Weighted Balancing";
  }
}
