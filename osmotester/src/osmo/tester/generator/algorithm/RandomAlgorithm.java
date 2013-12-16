package osmo.tester.generator.algorithm;

import osmo.common.Randomizer;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.List;

/**
 * A simple algorithm that randomly picks a step from the given set.
 *
 * @author Teemu Kanstren
 */
public class RandomAlgorithm implements FSMTraversalAlgorithm {
  /** Instance to provide deterministic random values. */
  private Randomizer rand = null;

  public RandomAlgorithm() {
  }

  @Override
  public void init(long seed, FSM fsm) {
  }

  @Override
  public FSMTransition choose(TestSuite suite, List<FSMTransition> choices) {
    return rand.oneOf(choices);
  }

  @Override
  public void initTest(long seed) {
    //+1000 is here to avoid giving same seed to this algorithm and valueset objects
    //if this is done and the valueset has the same number of options as the model has steps
    //the valueset will produce exact same value for that step (at least if it is first..)
    //when valueset.random method is used. yes it has happened with randomvaluemodel4 and unit tests..
    this.rand = new Randomizer(seed+1000);
  }

  @Override
  public FSMTraversalAlgorithm cloneMe() {
    return new RandomAlgorithm();
  }
}
