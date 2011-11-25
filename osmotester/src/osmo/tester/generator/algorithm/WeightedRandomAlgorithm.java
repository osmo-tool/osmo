package osmo.tester.generator.algorithm;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static osmo.common.TestUtils.rawWeightedRandomFrom;

/**
 * Implements a more realistic weighted random algorithm than {@link WeightedLessRandomAlgorithm}.
 * That is, a transition is picked from the given set of options based on probability propertional to the weight of
 * each transition.
 *
 * @author Teemu Kanstren
 */
public class WeightedRandomAlgorithm implements FSMTraversalAlgorithm {
  private static final Logger log = new Logger(WeightedRandomAlgorithm.class);

  @Override
  public void init(FSM fsm) {
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    log.debug("choosing from:" + choices);
    Collections.sort(choices, new WeightComparator());
    List<Integer> weights = new ArrayList<Integer>();
    for (FSMTransition choice : choices) {
      weights.add(choice.getWeight());
    }
    int index = rawWeightedRandomFrom(weights);
    return choices.get(index);
  }
}
