package osmo.tester.gui.manualdrive;

import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/** @author Teemu Kanstren */
public class ManualEndCondition implements EndCondition {
  private boolean endTest = false;
  private boolean endSuite = false;
  private boolean strict = false;

  public ManualEndCondition() {
    EndConditionFrame frame = new EndConditionFrame(this);
    frame.setVisible(true);
  }

  public void endTest() {
    this.endTest = true;
  }

  public void endSuite() {
    this.endSuite = true;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return endSuite;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    boolean end = endTest;
    endTest = false;
    return end;
  }

  @Override
  public void init(FSM fsm) {
  }

  @Override
  public boolean isStrict() {
    return strict;
  }

  @Override
  public void setStrict(boolean strict) {
    this.strict = strict;
  }
}
