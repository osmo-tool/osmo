package osmo.tester.generator.endcondition;

import osmo.tester.model.FSM;

/**
 * @author Teemu Kanstren
 */
public class AndComposition implements EndCondition {
  private final EndCondition[] conditions;

  public AndComposition(EndCondition... conditions) {
    this.conditions = conditions;
  }

  @Override
  public boolean endNow(FSM fsm, boolean evaluateSuite) {
    boolean result = true;
    for (EndCondition condition : conditions) {
      if (!condition.endNow(fsm, evaluateSuite)) {
        result = false;
        break;
      }
    }
    return result;
  }
}
