package osmo.tester.generator.endcondition.logical;

import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.Arrays;

/**
 * Allows one to compose several end conditions into a single one, where both of the conditions must be met before
 * test generation should end.
 *
 * @author Teemu Kanstren
 */
public class And implements EndCondition {
  /** The set of end conditions to be checked. */
  private final EndCondition[] conditions;

  /** @param conditions The set of end conditions to be checked. */
  public And(EndCondition... conditions) {
    this.conditions = conditions;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    for (EndCondition condition : conditions) {
      if (!condition.endSuite(suite, fsm)) {
        //if any return "false", the AND composition becomes false as well.
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    for (EndCondition condition : conditions) {
      if (!condition.endTest(suite, fsm)) {
        //if any return "false", the AND composition becomes false as well.
        return false;
      }
    }
    return true;
  }

  @Override
  public void init(FSM fsm) {
    for (EndCondition condition : conditions) {
      condition.init(fsm);
    }
  }

  @Override
  public String toString() {
    return "And{" +
            "conditions=" + (conditions == null ? null : Arrays.asList(conditions)) +
            '}';
  }
}
