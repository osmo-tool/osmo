package osmo.tester.generator.endcondition;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
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
    int suiteLength = suite.currentTestNumber();
    log.d("finished tests:" + suiteLength + " current steps:" + suite.currentSteps());
    //suite maintains length of size + 1, but at this point the previous one has just ended, or so we think..
    return suiteLength >= length;
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    int currentSteps = suite.currentSteps();
    log.d(" et:" + suite.getAllTestCases().size() + " c:" + currentSteps);
    return currentSteps >= length;
  }

  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public String toString() {
    return "Length{" +
            "length=" + length +
            '}';
  }

  @Override
  public EndCondition cloneMe() {
    //we have no state to initialize so can just return self
    return this;
  }
}
