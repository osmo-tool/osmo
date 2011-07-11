package osmo.tester.generator.endcondition;

import osmo.tester.model.FSM;

/**
 * Allows one to compose several end conditions into a single one, where one of the conditions must be met before
 * test generation should end.
 *
 * @author Teemu Kanstren
 */
public class OrComposition implements EndCondition {
  /** The set of end conditions to be checked. */
  private final EndCondition[] conditions;

  /**
   * Costructor.
   *
   * @param conditions The set of end conditions to be checked.
   */
  public OrComposition(EndCondition... conditions) {
    this.conditions = conditions;
  }

  @Override
  public boolean endNow(FSM fsm, boolean evaluateSuite) {
    for (EndCondition condition : conditions) {
      if (condition.endNow(fsm, evaluateSuite)) {
        //if any return true, the OR composition becomes true as well
        return true;
      }
    }
    return false;
  }
}
