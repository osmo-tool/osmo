package osmo.tester.generator.algorithm;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.parser.ParserResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements a simple weighted random algorithm.
 * That is, a transition is picked from the given set of options based on probability proportional to the weight of
 * each transition.
 *
 * @author Teemu Kanstren
 */
public class WeightedRandomAlgorithm implements FSMTraversalAlgorithm {
  private static final Logger log = new Logger(WeightedRandomAlgorithm.class);
  /** Randomizer instance that allows us to run stuff in parallel vs static imports. */
  private final Randomizer rand;

  public WeightedRandomAlgorithm() {
    this.rand = new Randomizer(OSMOConfiguration.getSeed());
  }

  @Override
  public void init(ParserResult parserResult) {
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    log.debug("choosing from:" + choices);
    Collections.sort(choices, new WeightComparator());
    List<Integer> weights = new ArrayList<>();
    for (FSMTransition choice : choices) {
      weights.add(choice.getWeight());
    }
    int index = rand.rawWeightedRandomFrom(weights);
    return choices.get(index);
  }
}
