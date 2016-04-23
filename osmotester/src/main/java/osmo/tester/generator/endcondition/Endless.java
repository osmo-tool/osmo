package osmo.tester.generator.endcondition;

import osmo.tester.OSMOConfiguration;
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
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public EndCondition cloneMe() {
    //we have no state to initialize so can just return self
    return this;
  }
}
