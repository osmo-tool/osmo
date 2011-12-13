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

import static osmo.common.TestUtils.oneOf;

/**
 * A test generation algorithm that is similar to the {@link BalancingAlgorithm}
 * but also takes into account weights assigned to transitions. For example, consider a model that has 2 transitions A and B,
 * and both have been visited twice. Now if A has a weight of 2 and B a weight of 3, the algorithm favors B due to
 * its combined weight and coverage values. The formula is (number of times a transition is visited/transition weight).
 * From the resulting set of values, the one with the smallest score is taken. If several share the same score, the
 * choice of transition is random from these.
 * <p/>
 * Note than in calculation, a transition that is never visited has a visited value of 1 (and one that is visited once
 * has a value of 2 and so on) to allow for simplified calculation of transition visit scores (otherwise all
 * transitions would start with score of 0, and the choices of first transitions would be random without weight).
 *
 * @author Teemu Kanstren
 */
public class WeightedLessRandomAlgorithm implements FSMTraversalAlgorithm {
  private static final Logger log = new Logger(WeightedLessRandomAlgorithm.class);

  @Override
  public void init(FSM fsm) {
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    log.debug("choosing from:" + choices);
    //count weighted score for all transitions in the current test suite as well as any new ones in the list of transitions
    Map<FSMTransition, Double> scores = countScore(history, choices);
    double smallest = Integer.MAX_VALUE;
    //find the lowest score
    for (FSMTransition transition : choices) {
      Double score = scores.get(transition);
      if (score < smallest) {
        smallest = score;
      }
    }
    //create list of actual options, being the ones with the lowest scores and in the set of choices
    Collection<FSMTransition> options = new ArrayList<FSMTransition>();
    for (FSMTransition transition : choices) {
      if (scores.get(transition) == smallest) {
        options.add(transition);
      }
    }
    if (options.size() == 0) {
      options = choices;
    }
    return oneOf(options);
  }

  /**
   * Counts the "score" of a set of transition. The transition with the highest score should be taken first and the
   * one with the lowest last. See class header for formula description and notes on why visit values start with 1.
   * <p/>
   * Note that this is typically recalculated between each transition since the one with
   * the highest score may be also the highest in the next round and we cannot simply take them in order from a single
   * calculation (even if the available set was the same).
   *
   * @param history   The test generation history.
   * @param available The set of available transitions (for which scores are calculated).
   * @return A mapping of transitions to their scores.
   */
  private Map<FSMTransition, Double> countScore(TestSuite history, List<FSMTransition> available) {
    Map<FSMTransition, Integer> coverage = new HashMap<FSMTransition, Integer>(1);
    List<TestCase> tests = history.getAllTestCases();
    //first we count how many times a transition has been covered
    for (TestCase test : tests) {
      List<TestStep> steps = test.getSteps();
      for (TestStep step : steps) {
        FSMTransition transition = step.getTransition();
        Integer count = coverage.get(transition);
        if (count == null) {
          //we use 1 as the starting value since 0 divided by any weight would be 0 and mess up the model initialization
          count = 1;
        }
        coverage.put(transition, count + 1);
      }
    }
    //if one was never covered, we set it to default start value of 1 to get correct values overall
    for (FSMTransition transition : available) {
      if (coverage.get(transition) == null) {
        coverage.put(transition, 1);
      }
    }
    log.debug("coverage" + coverage);
    Map<FSMTransition, Double> scores = new HashMap<FSMTransition, Double>();
    //then we divide each score by the weight of the transition
    Set<FSMTransition> transitions = coverage.keySet();
    for (FSMTransition transition : transitions) {
      double score = coverage.get(transition);
      score /= transition.getWeight();
      scores.put(transition, score);
    }
    log.debug("weighted scores:" + scores);
    return scores;
  }
}
