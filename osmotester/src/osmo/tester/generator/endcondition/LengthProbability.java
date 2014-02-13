package osmo.tester.generator.endcondition;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.endcondition.logical.And;
import osmo.tester.generator.endcondition.logical.Or;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/** 
 * Defines both a minimum length and optional maximum length for test/suite, and the probability to stop
 * once this minimum length has been achieved.
 * For example, with minimum length of 3 and probability of 0.2, the generated tests are always at least 3 steps
 * long and after that end with a probability of 20% at each step.
 *
 * The probability must be between 0..1 where 0 means the generation is never stopped
 * and 1 means the generation is always stopped.
 * As an example, value 0.2 means the probability is 20%, that is the value from the random number generator must
 * be below 0.2 to signal stop.
 * 
 * In practice this creates a combined And/Or end condition which create combinations of different Length/Probability
 * end conditions to set the minimum/maximum lengths and the probability to stop after minimum length.
 * The point is simply to make it simple to create the otherwise complex combination.
 * 
 * @author Teemu Kanstren 
 */
public class LengthProbability implements EndCondition {
  /** The combining And/Or end condition to which the parameters and requests are delegated. */
  private EndCondition delegate = null;
  private int min;
  private int max;
  private double probability;

  public LengthProbability(int minLength, double probability) {
    init(minLength, 0, probability);
  }
  
  public LengthProbability(int minLength, int maxLength, double probability) {
    init(minLength, maxLength, probability);
  }

  private void init(int minLength, int maxLength, double probability) {
    this.min = minLength;
    this.max = maxLength;
    this.probability = probability;
    if (maxLength < 0) {
      String msg = "Maximum length cannot be negative. Given (" + maxLength + "). Use 0 to disable max length.";
      throw new IllegalArgumentException(msg);
    }
    if (maxLength > 0) {
      if (minLength > maxLength) {
        String msg = "Given minimum length (" + minLength + ") greater than maximum length ("+maxLength+"). " +
                "Must be the other way around.";
        throw new IllegalArgumentException(msg);
      }
      //(length >= min && probability > threshold) || length == max)
      delegate = new Or(new And(new Length(minLength), new Probability(probability)), new Length(maxLength));
    } else {
      delegate = new And(new Length(minLength), new Probability(probability));
    }
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return delegate.endSuite(suite, fsm);
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return delegate.endTest(suite, fsm);
  }

  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
    delegate.init(seed, fsm, config);
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }

  public double getProbability() {
    return probability;
  }
}
