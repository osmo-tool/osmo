package osmo.tester.generator.endcondition;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;

import java.util.Random;

/**
 * A simple end condition that takes a probability threshold for stopping test generation.
 * The probability needs to be between 0..1 where 0 means the generation is never stopped
 * and 1 means the generation is always stopped.
 * 
 * @author Teemu Kanstren
 */
public class Probability implements EndCondition {
  private static Logger log = new Logger(Probability.class);
  /** The stopping threshold. */
  private final double threshold;
  /** Used to generate random values to check if generation should be stopped. */
  private final Random random = new Random();

  /**
   * Constructor.
   *
   * @param threshold The threshold value, if this is exceeded in evaluation, generation is stopped.
   */
  public Probability(double threshold) {
    if (threshold < 0 || threshold > 1) {
      throw new IllegalArgumentException(Probability.class.getSimpleName()+" threshold must be between 0 and 1. Was "+threshold+".");
    }
    this.threshold = threshold;
  }

  public boolean endNow(TestSuite suite, FSM fsm) {
    double v = random.nextDouble();
    log.debug("value "+v+" threshold "+threshold);
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
}
