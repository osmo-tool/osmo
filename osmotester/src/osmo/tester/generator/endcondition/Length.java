package osmo.tester.generator.endcondition;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/**
 * Stops test generation when a number of test steps has been generated for the test case,
 * or suite generation when a number of test cases in the suite has been generated.
 *
 * @author Teemu Kanstren
 */
public class Length implements EndCondition {
  private static final Logger log = new Logger(Length.class);
  /** The stopping length. */
  private final int length;

  /**
   * @param length The number of steps/tests when to stop generation.
   */
  public Length(int length) {
    if (length < 0) {
      throw new IllegalArgumentException("Length cannot be < 0, was " + length + ".");
    }
    this.length = length;
  }

  public int getLength() {
    return length;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    log.debug("finished tests:" + suite.getFinishedTestCases().size() + " current steps:" + suite.currentSteps());
    return suite.getFinishedTestCases().size() >= length;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    log.debug(" et:" + suite.getFinishedTestCases().size() + " c:" + suite.currentSteps());
    return suite.currentSteps() >= length;
  }

  @Override
  public void init(long seed, FSM fsm) {
  }

  @Override
  public String toString() {
    return "Length{" +
            "length=" + length +
            '}';
  }
}
