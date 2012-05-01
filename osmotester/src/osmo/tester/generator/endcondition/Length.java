package osmo.tester.generator.endcondition;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/**
 * A simple end condition to stop test case generation when a number of test steps has been generated,
 * or to stop test suite generation when a number of test cases in the suite has been generated.
 *
 * @author Teemu Kanstren
 */
public class Length extends AbstractEndCondition {
  private static Logger log = new Logger(Length.class);
  /** The stopping length. */
  private final int length;

  /**
   * Constructor.
   *
   * @param length The number of steps/tests when to stop generation.
   */
  public Length(int length) {
    if (length < 0) {
      throw new IllegalArgumentException("Length cannot be < 0, was " + length + ".");
    }
    this.length = length;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    log.debug(" es:" + suite.getFinishedTestCases().size() + " c:" + suite.currentSteps());
    return suite.getFinishedTestCases().size() >= length;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    log.debug(" et:" + suite.getFinishedTestCases().size() + " c:" + suite.currentSteps());
    return suite.currentSteps() >= length;
  }

  @Override
  public void init(FSM fsm) {
  }

  @Override
  public String toString() {
    return "Length{" +
            "length=" + length +
            '}';
  }
}
