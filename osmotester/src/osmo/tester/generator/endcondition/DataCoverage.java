package osmo.tester.generator.endcondition;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/**
 * @author Teemu Kanstren
 */
public class DataCoverage implements EndCondition {
  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return false;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return false;
  }
}
