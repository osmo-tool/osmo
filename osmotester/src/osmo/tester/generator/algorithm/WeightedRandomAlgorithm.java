package osmo.tester.generator.algorithm;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.parser.ParserResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The chosen step is based on probability proportional to the weight of each step.
 *
 * @author Teemu Kanstren
 */
public class WeightedRandomAlgorithm implements FSMTraversalAlgorithm {
  private static final Logger log = new Logger(WeightedRandomAlgorithm.class);
  /** Randomizer instance that allows us to run stuff in parallel vs static imports. */
  private Randomizer rand = null;

  public WeightedRandomAlgorithm() {
  }

  @Override
  public void init(long seed, FSM fsm) {
    this.rand = new Randomizer(seed);
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    log.debug("choosing from:" + choices);
    //sort the list to have smallest weights first, to provide correct parameter list to rawWeightedRandomFrom()
    Collections.sort(choices, new WeightComparator());
    List<Integer> weights = new ArrayList<>();
    for (FSMTransition choice : choices) {
      weights.add(choice.getWeight());
    }
    int index = rand.rawWeightedRandomFrom(weights);
    return choices.get(index);
  }

  @Override
  public void initTest() {
  }
}
