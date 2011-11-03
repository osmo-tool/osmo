package osmo.tester.generator.endcondition;

import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/**
 * A simple end condition that takes a probability threshold for stopping test generation.
 * The probability needs to be between 0..1 where 0 means the generation is never stopped
 * and 1 means the generation is always stopped.
 *
 * @author Teemu Kanstren
 */
public class Probability extends AbstractEndCondition {
  private static Logger log = new Logger(Probability.class);
  /** The stopping threshold. */
  private final double threshold;

  /**
   * Constructor.
   *
   * @param threshold The threshold value, if this is exceeded in evaluation, generation is stopped.
   */
  public Probability(double threshold) {
    if (threshold < 0 || threshold > 1) {
      throw new IllegalArgumentException(Probability.class.getSimpleName() + " threshold must be between 0 and 1. Was " + threshold + ".");
    }
    this.threshold = threshold;
  }

  public boolean endNow(TestSuite suite, FSM fsm) {
    double v = TestUtils.getRandom().nextDouble();
    log.debug("value " + v + " threshold " + threshold);
    return v <= threshold;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return endNow(suite, fsm);
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return endNow(suite, fsm);
  }

  @Override
  public void init(FSM fsm) {
  }
}
