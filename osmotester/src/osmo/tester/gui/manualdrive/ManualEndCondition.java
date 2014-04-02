package osmo.tester.gui.manualdrive;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/** @author Teemu Kanstren */
public class ManualEndCondition implements EndCondition {
  private boolean endSuite = false;
  private boolean endTest = false;

  public boolean isEndSuite() {
    return endSuite;
  }

  public void setEndSuite(boolean endSuite) {
    this.endSuite = endSuite;
  }

  public boolean isEndTest() {
    return endTest;
  }

  public void setEndTest(boolean endTest) {
    this.endTest = endTest;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return endSuite;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return endTest;
  }

  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
  }
}
