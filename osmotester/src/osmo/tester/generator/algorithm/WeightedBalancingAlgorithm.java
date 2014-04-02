package osmo.tester.generator.algorithm;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
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
 * <p/>
 * The formula is:
 * -for each possible choice calculate: test step weight / number of times step covered
 * -multiply these values until you get a value over 10
 * -use the resulting values as the "score" to pick one of the test steps (as weighted random choice, score=weight)
 * <p/>
 * Thus steps with same coverage and same weight = same probability to be taken next.
 * Steps with same coverage but different weight = one with higher weight has higher probability to be taken next.
 * Steps with different coverage and different weight = which one gets higher score depends on how much bigger is
 * the weight and how much bigger is the coverage on either of the choices.
 * <p/>
 * Note than in calculation, a step that is never visited has a visited value of 1 (and one that is visited once
 * has a value of 2 and so on) to allow for simplified calculation of test step scores. Otherwise all
 * test steps would start with score of 0, and the choices of first step would be random without weight.
 *
 * @author Teemu Kanstren
 */
public class WeightedBalancingAlgorithm implements FSMTraversalAlgorithm {
  private static final Logger log = new Logger(WeightedBalancingAlgorithm.class);
  /** Keeps a list of how many times each transition has been covered. */
  private Map<String, Integer> coverage;
  /** Provides random values. */
  private Randomizer rand = null;

  public WeightedBalancingAlgorithm() {
  }

  @Override
  public void init(long seed, FSM fsm) {
    coverage = new LinkedHashMap<>(fsm.getTransitions().size());
  }

  @Override
  public FSMTransition choose(TestSuite suite, List<FSMTransition> choices) {
    log.debug("choosing from:" + choices);
    //count weighted score for all transitions taken so far as well as any new choices
    Map<FSMTransition, Double> scoreMap = countScore(choices);

    //list of all steps known
    List<FSMTransition> steps = new ArrayList<>();
    //list of weights for the steps, in the same order so index for step is always the same
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
    //create a suitable argument list for rawWeightedRandomFrom
    List<Integer> scores = new ArrayList<>();
    for (double tempScore : tempScores) {
      scores.add((int) (Math.round(tempScore)));
    }

    //find the choice
    int index = rand.rawWeightedRandomFrom(scores);
    FSMTransition transition = steps.get(index);
    updateCoverage(transition);
    return transition;
  }

  private void updateCoverage(FSMTransition transition) {
    Integer count = coverage.get(transition.getStringName());
    if (count == null) {
      //we use 1 as the starting value since 0 divided by any weight would be 0 and mess up the model initialization
      count = 1;
    }
    coverage.put(transition.getStringName(), count + 1);
  }

  /**
   * Counts the "score" of a set of steps. The step with the highest score should be taken first and the
   * one with the lowest last. See class header for formula description and notes on why visit values start with 1.
   * <p/>
   * Note that this is typically recalculated between each step taken since the one with
   * the highest score may be also the highest in the next round and we cannot simply take them in order from a single
   * calculation (even if the available set was the same).
   *
   * @param available The set of available steps (for which scores are calculated).
   * @return A mapping of steps to their scores.
   */
  private Map<FSMTransition, Double> countScore(List<FSMTransition> available) {
    int min = Integer.MAX_VALUE;
    //if one was never covered, we set it to default start value of 1 to get correct values overall
    for (FSMTransition transition : available) {
      String name = transition.getStringName();
      if (coverage.get(name) == null) {
        coverage.put(name, 1);
      }
      if (coverage.get(name) < min) {
        //find lowest coverage value
        min = coverage.get(name);
      }
    }
    log.debug("coverage" + coverage);
    Map<FSMTransition, Double> scores = new LinkedHashMap<>();
    //then we count step score by dividing its weight by its coverage value
    Set<String> transitions = coverage.keySet();
    for (String name : transitions) {
      FSMTransition transition = null;
      for (FSMTransition a : available) {
        if (a.getStringName().equals(name)) {
          transition = a;
          break;
        }
      }
      if (transition == null) {
        continue;
      }
      double score = transition.getWeight();
      score /= coverage.get(transition.getStringName());
      scores.put(transition, score);
    }
    log.debug("weighted scores:" + scores);
    return scores;
  }

  @Override
  public void initTest(long seed) {
    this.rand = new Randomizer(seed);
  }

  @Override
  public FSMTraversalAlgorithm cloneMe() {
    return new WeightedBalancingAlgorithm();
  }
}
