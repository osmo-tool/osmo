package osmo.tester.generator.algorithm;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static osmo.common.TestUtils.*;

/**
 * A test generation algorithm that is similar to the {@link BalancingAlgorithm}
 * but also takes into account weights assigned to transitions. For example, consider a model that has 2 transitions A and B,
 * and both have been visited twice. Now if A has a weight of 2 and B a weight of 3, the algorithm favors B due to
 * its combined weight and coverage values. 
 * THe formula is: 
 * -for each possible choice: transition weight / number of times transitions covered
 * -multiply coverage values until you get a values with some over 1000
 * -use the resulting values as the "weight" to pick one of the transitions
 * <p/>
 * Note than in calculation, a transition that is never visited has a visited value of 1 (and one that is visited once
 * has a value of 2 and so on) to allow for simplified calculation of transition visit scores (otherwise all
 * transitions would start with score of 0, and the choices of first transitions would be random without weight).
 *
 * @author Teemu Kanstren
 */
public class WeightedBalancingAlgorithm implements FSMTraversalAlgorithm {
  private static final Logger log = new Logger(WeightedBalancingAlgorithm.class);
  private Map<FSMTransition, Integer> coverage;

  @Override
  public void init(FSM fsm) {
    coverage = new HashMap<>(fsm.getTransitions().size());
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    log.debug("choosing from:" + choices);
    //count weighted score for all transitions in the current test suite as well as any new ones in the list of transitions
    Map<FSMTransition, Double> scoreMap = countScore(choices);

    List<FSMTransition> steps = new ArrayList<>();
    double[] tempScores = new double[scoreMap.size()];
    int i = 0;
    for (Map.Entry<FSMTransition, Double> entry : scoreMap.entrySet()) {
      steps.add(entry.getKey());
      tempScores[i++] = entry.getValue();
    }

    //here we multiply the scores until we get big enough integers than can be passed to rawWeightedRandomFrom()
    boolean done = false;
    while (!done) {
      done = true;
      for (int j = 0; j < tempScores.length; j++) {
        tempScores[j] *= 10000;
        if (tempScores[j] < 10) {
          done = false;
        }
      }
    }
    List<Integer> scores = new ArrayList<>();
    for (double tempScore : tempScores) {
      scores.add((int) (Math.round(tempScore)));
    }

    int index = rawWeightedRandomFrom(scores);
    FSMTransition transition = steps.get(index);
    updateCoverage(transition);
    return transition;
  }

  private void updateCoverage(FSMTransition transition) {
    Integer count = coverage.get(transition);
    if (count == null) {
      //we use 1 as the starting value since 0 divided by any weight would be 0 and mess up the model initialization
      count = 1;
    }
    coverage.put(transition, count + 1);
  }

  /**
   * Counts the "score" of a set of transition. The transition with the highest score should be taken first and the
   * one with the lowest last. See class header for formula description and notes on why visit values start with 1.
   * <p/>
   * Note that this is typically recalculated between each transition since the one with
   * the highest score may be also the highest in the next round and we cannot simply take them in order from a single
   * calculation (even if the available set was the same).
   *
   * @param available The set of available transitions (for which scores are calculated).
   * @return A mapping of transitions to their scores.
   */
  private Map<FSMTransition, Double> countScore(List<FSMTransition> available) {
    Map<FSMTransition, Integer> coverage = new HashMap<>(1);
    int min = Integer.MAX_VALUE;
    //if one was never covered, we set it to default start value of 1 to get correct values overall
    for (FSMTransition transition : available) {
      if (coverage.get(transition) == null) {
        coverage.put(transition, 1);
      }
      if (coverage.get(transition) < min) {
        min = coverage.get(transition);
      }
    }
    log.debug("coverage" + coverage);
    Map<FSMTransition, Double> scores = new HashMap<>();
    //then we divide each score by the weight of the transition
    Set<FSMTransition> transitions = coverage.keySet();
    for (FSMTransition transition : transitions) {
      double score = transition.getWeight();
      //+1 is needed to avoid multiplication by 0, which would lead the highest weighted never to happen
      score /= coverage.get(transition);
      scores.put(transition, score);
    }
    log.debug("weighted scores:" + scores);
    return scores;
  }
}
