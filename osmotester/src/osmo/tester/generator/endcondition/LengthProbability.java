package osmo.tester.generator.endcondition;

import osmo.tester.generator.endcondition.logical.And;
import osmo.tester.generator.endcondition.logical.Or;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/** @author Teemu Kanstren */
public class LengthProbability extends AbstractEndCondition {
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
  public void init(FSM fsm) {
  }
}
