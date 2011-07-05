package osmo.tester.generator.endcondition;

import osmo.tester.model.FSM;

/**
 * @author Teemu Kanstren
 */
public class OrComposition implements EndCondition {
  private final EndCondition[] conditions;

  public OrComposition(EndCondition... conditions) {
    this.conditions = conditions;
  }

  @Override
  public boolean endNow(FSM fsm, boolean evaluateSuite) {
    boolean result = false;
    for (EndCondition condition : conditions) {
      if (condition.endNow(fsm, evaluateSuite)) {
        result = true;
        break;
      }
    }
    return result;
  }
}
