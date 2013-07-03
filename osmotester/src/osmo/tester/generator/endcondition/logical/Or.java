package osmo.tester.generator.endcondition.logical;

import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.Arrays;

/**
 * Allows one to compose several end conditions into a single one, where one of the conditions must be met before
 * test generation should end.
 *
 * @author Teemu Kanstren
 */
public class Or implements EndCondition {
  /** The set of end conditions to be checked. */
  private final EndCondition[] conditions;

  /** @param conditions The set of end conditions to be checked. */
  public Or(EndCondition... conditions) {
    this.conditions = conditions;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    for (EndCondition condition : conditions) {
      if (condition.endSuite(suite, fsm)) {
        //if any return true, the OR composition becomes true as well
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    for (EndCondition condition : conditions) {
      if (condition.endTest(suite, fsm)) {
        //if any return true, the OR composition becomes true as well
        return true;
      }
    }
    return false;
  }

  @Override
  public void init(long seed, FSM fsm) {
    for (EndCondition condition : conditions) {
      condition.init(seed, fsm);
    }
  }

  @Override
  public String toString() {
    return "Or{" +
            "conditions=" + (conditions == null ? null : Arrays.asList(conditions)) +
            '}';
  }
}
