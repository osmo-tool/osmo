package osmo.tester.generator.endcondition;

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
 * In practice this creates a combined And/Or end condition which create combinations of different Length/Probability
 * end conditions to set the minimum/maximum lengths and the probability to stop after minimum length.
 * The point is simply to make it simple to create the otherwise complex combination.
 * 
 * @author Teemu Kanstren 
 */
public class LengthProbability implements EndCondition {
  /** The combining And/Or end condition to which the parameters and requests are delegated. */
  private EndCondition delegate = null;

  public LengthProbability(int minLength, double probability) {
    init(minLength, -1, probability);
  }
  
  public LengthProbability(int minLength, int maxLength, double probability) {
    init(minLength, maxLength, probability);
  }

  private void init(int minLength, int maxLength, double probability) {
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
  public void init(long seed, FSM fsm) {
    delegate.init(seed, fsm);
  }
}
