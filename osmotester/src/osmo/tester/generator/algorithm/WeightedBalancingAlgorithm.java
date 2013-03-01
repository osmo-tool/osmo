package osmo.tester.generator.algorithm;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.parser.ParserResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A test generation algorithm that is similar to the {@link BalancingAlgorithm} but also takes into account
 * weights assigned to test steps. This algorithm does not consider step-pairs, it only balances the steps according
 * to their frequency in the trace so far and the weight they have been given.
 * <p/>
 * For example, consider a model that has 2 steps A and B, and both have been visited twice.
 * Now if A has a weight of 2 and B a weight of 3, the algorithm favors B due to its combined weight and coverage values.
 * The formula is:
 * -for each possible choice: test step weight / number of times step covered
 * -multiply coverage values until you get a value over 10
 * -use the resulting values as the "weight" to pick one of the test steps
 * <p/>
 * Note than in calculation, a step that is never visited has a visited value of 1 (and one that is visited once
 * has a value of 2 and so on) to allow for simplified calculation of test step scores (otherwise all
 * test steps would start with score of 0, and the choices of first step would be random without weight).
 *
 * @author Teemu Kanstren
 */
public class WeightedBalancingAlgorithm implements FSMTraversalAlgorithm {
  private static final Logger log = new Logger(WeightedBalancingAlgorithm.class);
  /** Keeps a list of how many times each transition has been covered. */
  private Map<FSMTransition, Integer> coverage;
  /** For randomization. Specific instance to allow parallel executions inside single VM. */
  private final Randomizer rand;

  public WeightedBalancingAlgorithm() {
    this.rand = new Randomizer(OSMOConfiguration.getSeed());
  }

  @Override
  public void init(ParserResult parserResult) {
    coverage = new HashMap<>(parserResult.getFsm().getTransitions().size());
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    log.debug("choosing from:" + choices);
    //count weighted score for all transitions taken so far as well as any new choices
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
      for (int j = 0 ; j < tempScores.length ; j++) {
        tempScores[j] *= 10000;
        if (tempScores[j] < 10) {
          done = false;
        }
      }
    }
    //create a suitable argument list
    List<Integer> scores = new ArrayList<>();
    for (double tempScore : tempScores) {
      scores.add((int) (Math.round(tempScore)));
    }

    int index = rand.rawWeightedRandomFrom(scores);
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
    Map<FSMTransition, Double> scores = new LinkedHashMap<>();
    //then we divide each score by the weight of the transition
    Set<FSMTransition> transitions = coverage.keySet();
    for (FSMTransition transition : transitions) {
      double score = transition.getWeight();
      score /= coverage.get(transition);
      scores.put(transition, score);
    }
    log.debug("weighted scores:" + scores);
    return scores;
  }
}
