package osmo.tester.generator.endcondition;

import osmo.common.Randomizer;
import osmo.common.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/**
 * Defines a probability threshold for stopping test generation.
 * The probability must be between 0..1 where 0 means the generation is never stopped
 * and 1 means the generation is always stopped.
 * As an example, value 0.2 means the probability is 20%, that is the value from the random number generator must
 * be below 0.2 to signal stop.
 *
 * @author Teemu Kanstren
 */
public class Probability implements EndCondition {
  private static final Logger log = new Logger(Probability.class);
  /** The stopping threshold. */
  private final double threshold;
  /** For random numbers. */
  private Randomizer rand = null;

  /** @param threshold The threshold value, if this is exceeded in evaluation, generation is stopped. */
  public Probability(double threshold) {
    if (threshold < 0 || threshold > 1) {
      throw new IllegalArgumentException(Probability.class.getSimpleName() + " threshold must be between 0 and 1. Was " + threshold + ".");
    }
    this.threshold = threshold;
  }

  public boolean endNow(TestSuite suite, FSM fsm) {
    double v = rand.nextDouble();
    log.d("value " + v + " threshold " + threshold);
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
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
    rand = new Randomizer(seed);
  }

  public double getThreshold() {
    return threshold;
  }

  @Override
  public String toString() {
    return "Probability{" +
            "threshold=" + threshold +
            '}';
  }

  public Randomizer getRandomizer() {
    return rand;
  }

  @Override
  public EndCondition cloneMe() {
    Probability clone = new Probability(threshold);
//    clone.rand = new Randomizer(this.rand.getSeed());
    return clone;
  }
}
