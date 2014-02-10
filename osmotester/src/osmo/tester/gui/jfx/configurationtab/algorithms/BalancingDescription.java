package osmo.tester.gui.jfx.configurationtab.algorithms;

import osmo.tester.generator.algorithm.BalancingAlgorithm;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;

/**
 * @author Teemu Kanstren
 */
public class BalancingDescription implements AlgorithmDescription {
  @Override
  public FSMTraversalAlgorithm getAlgorithm() {
    return new BalancingAlgorithm();
  }

  @Override
  public String toString() {
    return "Balancing";
  }
}
