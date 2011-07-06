package osmo.tester.generator.endcondition;

import osmo.tester.model.FSM;

/**
 * This causes test generation to go on basically forever. Only way to stop the test case/test suite generation
 * when using this strategy is to provide an {@link osmo.tester.annotation.EndCondition} yourself.
 *
 * @author Teemu Kanstren
 */
public class EndlessCondition implements EndCondition {
  @Override
  public boolean endNow(FSM fsm, boolean evaluateSuite) {
    return false;
  }
}
