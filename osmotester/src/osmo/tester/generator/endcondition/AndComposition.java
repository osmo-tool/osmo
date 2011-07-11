package osmo.tester.generator.endcondition;

import osmo.tester.model.FSM;

/**
 * Allows one to compose several end conditions into a single one, where both of the conditions must be met before
 * test generation should end.
 *
 * @author Teemu Kanstren
 */
public class AndComposition implements EndCondition {
  /** The set of end conditions to be checked. */
  private final EndCondition[] conditions;

  /**
   * Costructor.
   *
   * @param conditions The set of end conditions to be checked.
   */
  public AndComposition(EndCondition... conditions) {
    this.conditions = conditions;
  }

  @Override
  public boolean endNow(FSM fsm, boolean evaluateSuite) {
    boolean result = true;
    for (EndCondition condition : conditions) {
      if (!condition.endNow(fsm, evaluateSuite)) {
        //if any return "false", the AND composition becomes false as well.
        return false;
      }
    }
    return true;
  }
}
