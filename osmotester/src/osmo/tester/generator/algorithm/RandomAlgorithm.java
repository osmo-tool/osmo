package osmo.tester.generator.algorithm;

import osmo.common.Randomizer;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.parser.ParserResult;

import java.util.List;

/**
 * A simple algorithm that randomly picks a transition from the given set.
 * Seed is the usual seed for OSMO Tester.
 * Typically configured through OSMOConfiguration.setSeed().
 *
 * @author Teemu Kanstren
 */
public class RandomAlgorithm implements FSMTraversalAlgorithm {
  private final Randomizer rand;

  public RandomAlgorithm() {
    this.rand = new Randomizer(OSMOConfiguration.getSeed());
  }

  @Override
  public void init(ParserResult parserResult) {
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    return rand.oneOf(choices);
  }

  @Override
  public void initTest() {
  }
}
