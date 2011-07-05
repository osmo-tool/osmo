package osmo.tester.generator.strategy;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;

import java.util.Random;

/**
 * A simple strategy that takes a probability threshold for stopping test generation.
 * The probability needs to be between 0..1 where 1 means the generation is never stopped
 * and 0 means the generation is always stopped.
 * 
 * @author Teemu Kanstren
 */
public class ProbabilityStrategy implements ExitStrategy {
  private static Logger log = new Logger(ProbabilityStrategy.class);
  /** The stopping threshold. */
  private final double threshold;
  /** Used to generate random values to check if generation should be stopped. */
  private final Random random = new Random();

  /**
   * Constructor.
   *
   * @param threshold The threshold value, if this is exceeded in evaluation, generation is stopped.
   */
  public ProbabilityStrategy(double threshold) {
    if (threshold < 0 || threshold > 1) {
      throw new IllegalArgumentException(ProbabilityStrategy.class.getSimpleName()+" threshold must be between 0 and 1. Was "+threshold+".");
    }
    this.threshold = threshold;
  }

  @Override
  public boolean exitNow(FSM fsm, boolean evaluateSuite) {
    double v = random.nextDouble();
    log.debug("value "+v+" threshold "+threshold);
    return v >= threshold;
  }
}
