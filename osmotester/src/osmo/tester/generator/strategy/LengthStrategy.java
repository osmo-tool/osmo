package osmo.tester.generator.strategy;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;

/**
 * A simple strategy to stop test case generation when a number of test steps has been generated,
 * or to stop test suite generation when a number of test cases in the suite has been generated.
 * 
 * @author Teemu Kanstren
 */
public class LengthStrategy implements ExitStrategy {
  private static Logger log = new Logger(LengthStrategy.class);
   /** The stopping threshold. */
  private final int length;

  /**
   * Constructor.
   *
   * @param length The number of steps/tests when to stop generation.
   */
  public LengthStrategy(int length) {
    if (length < 0) {
      throw new IllegalArgumentException("Length cannot be < 0, was "+length+".");
    }
    this.length = length;
  }

  @Override
  public boolean exitNow(TestSuite testLog, boolean evaluateSuite) {
    log.debug("e:"+evaluateSuite+" h:"+testLog.getHistory().size()+" c:"+testLog.currentSteps());
    if (evaluateSuite) {
      return testLog.getHistory().size() >= length;
    }
    return testLog.currentSteps() >= length;
  }
}
