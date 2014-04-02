package osmo.tester.gui.jfx.configurationtab.algorithms;

import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;

/**
 * @author Teemu Kanstren
 */
public class RandomDescription implements AlgorithmDescription {
  @Override
  public FSMTraversalAlgorithm getAlgorithm() {
    return new RandomAlgorithm();
  }

  @Override
  public String toString() {
    return "Random";
  }
}
