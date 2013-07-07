package osmo.tester.generator.algorithm;

import osmo.common.Randomizer;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.parser.ParserResult;

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
    this.rand = new Randomizer(seed);
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    return rand.oneOf(choices);
  }

  @Override
  public void initTest() {
  }
}
