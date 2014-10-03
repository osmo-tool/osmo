package osmo.tester.gui.jfx.configurationtab.algorithms;

import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;

/**
 * @author Teemu Kanstren
 */
public class WeightedRandomDescription implements AlgorithmDescription {
  @Override
  public FSMTraversalAlgorithm getAlgorithm() {
    return new WeightedRandomAlgorithm();
  }

  @Override
  public String toString() {
    return "Weighted Balancing";
  }
}
