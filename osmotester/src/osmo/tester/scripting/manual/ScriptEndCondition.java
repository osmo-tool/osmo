package osmo.tester.scripting.manual;

import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.List;

/** @author Teemu Kanstren */
public class ScriptEndCondition implements EndCondition {
  private final ScriptAlgorithm algorithm;

  public ScriptEndCondition(ScriptAlgorithm algorithm) {
    this.algorithm = algorithm;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return algorithm.isSuiteDone();
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return algorithm.isTestDone();
  }

  @Override
  public void init(FSM fsm) {
  }

  @Override
  public boolean isStrict() {
    return true;
  }

  @Override
  public void setStrict(boolean strict) {
  }
}
