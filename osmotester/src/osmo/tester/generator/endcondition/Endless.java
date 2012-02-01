package osmo.tester.generator.endcondition;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/**
 * This causes test generation to go on basically forever. Only way to stop the test case/test suite generation
 * when using this end condition is to provide an {@link osmo.tester.annotation.EndCondition} yourself.
 *
 * @author Teemu Kanstren
 */
public class Endless implements EndCondition {
  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return false;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return false;
  }

  @Override
  public void init(FSM fsm) {
  }

  @Override
  public boolean isStrict() {
    return false;
  }

  @Override
  public void setStrict(boolean strict) {
  }
}
