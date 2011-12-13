package osmo.tester.generator.algorithm;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.List;

/**
 * Use the BalancingAlgorithm, this is an old placeholder that will be removed in the next version.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 * @deprecated Use BalancingAlgorithm instead.
 */
public class LessRandomAlgorithm implements FSMTraversalAlgorithm {
  private BalancingAlgorithm balancing = new BalancingAlgorithm();

  @Override
  public void init(FSM fsm) {
    balancing.init(fsm);
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    return balancing.choose(history, choices);
  }
}
