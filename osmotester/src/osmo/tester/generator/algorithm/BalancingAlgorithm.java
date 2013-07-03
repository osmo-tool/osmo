package osmo.tester.generator.algorithm;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.parser.ParserResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A test generation algorithm that is similar to the {@link RandomAlgorithm} but prefers to take
 * a the least covered of all available steps. Any uncovered steps are taken first, and after this
 * any uncovered pairs are taken. If all available steps and pairs have been taken already, the
 * algorithm picks one randomly but giving higher weight to those that have been covered fewer times.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class BalancingAlgorithm implements FSMTraversalAlgorithm {
  private static Logger log = new Logger(BalancingAlgorithm.class);
  /** The coverage for transitions pairs, key = source transition, value = {destination transition, coverage} */
  private Map<String, Map<FSMTransition, Integer>> tpCoverage = new LinkedHashMap<>();
  /** For randomization. Separate instances are used to allow multiple instances running concurrently. */
  private Randomizer rand = null;

  public BalancingAlgorithm() {
  }

  @Override
  public void init(long seed, ParserResult parserResult) {
    this.rand = new Randomizer(seed);
  }

  @Override
  public FSMTransition choose(TestSuite suite, List<FSMTransition> choices) {
    TestCaseStep ts = suite.getCurrentTest().getCurrentStep();
    String previous = null;
    if (ts != null) {
      previous = ts.getName();
    } else {
      previous = FSM.START_NAME;
    }
    initTPCoverage(previous, choices);


    FSMTransition choice = checkUncoveredSteps(suite, choices, previous);
    if (choice != null) {
      updateTPCoverage(previous, choice);
      return choice;
    }

    choice = checkUncoveredTP(previous, choices);
    if (choice != null) {
      updateTPCoverage(previous, choice);
      return choice;
    }

    choice = weightedChoice(previous, choices);
    updateTPCoverage(previous, choice);
    
    return choice;
  }

  private FSMTransition weightedChoice(String previous, List<FSMTransition> choices) {
    Map<FSMTransition, Integer> map = tpCoverage.get(previous);
    int[] weights = new int[choices.size()];
    int i = 0;
    int max = 0;
    for (FSMTransition choice : choices) {
      Integer weight = map.get(choice);
      weights[i++] = weight;
      if (weight > max) {
        max = weight;
      }
    }
    //add one to not get zeroes
    max++;
    //invert the weights
    for (i = 0 ; i < weights.length ; i++) {
      weights[i] = max - weights[i];
    }
    
    List<Integer> scores = new ArrayList<>();
    for (int weight : weights) {
      scores.add(weight);
    }
    int index = rand.rawWeightedRandomFrom(scores);
    return choices.get(index);
  }

  private FSMTransition checkUncoveredSteps(TestSuite suite, List<FSMTransition> choices, String previous) {
    //how many times each transition has been taken so far
    Map<String, Integer> tCoverage = suite.getTransitionCoverage();
    //we use a hashset to avoid duplicates from different calculations
    Collection<FSMTransition> options = new LinkedHashSet<>();
    options.addAll(choices);
    for (Iterator<FSMTransition> i = options.iterator() ; i.hasNext() ; ) {
      FSMTransition next = i.next();
      if (tCoverage.containsKey(next.getStringName())) {
        i.remove();
      }
    }
    log.debug("uncovered options:" + options);
    //options now contains all previously uncovered transitions
    if (options.size() > 0) {
      FSMTransition choice = rand.oneOf(options);
      return choice;
    }
    return null;
  }

  /**
   * Provides the transition pair coverage for the previously taken transition. That is, how many times other
   * transitions have been taken after the previously taken transition.
   * Also initializes the default value of 0 for any available transition that has not been taken, to make sure
   * that get() will always return something valid for the available choices.
   *
   * @param previous The transition that was taken previously (pair source).
   * @param choices  The choices for the next transition (pair destination).
   */
  private void initTPCoverage(String previous, List<FSMTransition> choices) {
    //this could be initialized once in init() but works so..
    Map<FSMTransition, Integer> currentTPCoverage = tpCoverage.get(previous);
    if (currentTPCoverage == null) {
      currentTPCoverage = new LinkedHashMap<>();
      tpCoverage.put(previous, currentTPCoverage);
    }
    for (FSMTransition t : choices) {
      if (currentTPCoverage.get(t) == null) {
        currentTPCoverage.put(t, 0);
      }
    }
  }

  /**
   * Provides a list of all previously uncovered pairs.
   *
   * @param previous Previously taken transition (pair source).
   * @param choices  The possible choices for the next transition to be taken (pair destination).
   */
  private FSMTransition checkUncoveredTP(String previous, Collection<FSMTransition> choices) {
    Collection<FSMTransition> uncoveredTP = new ArrayList<>();
    uncoveredTP.addAll(choices);
    Map<FSMTransition, Integer> map = tpCoverage.get(previous);
    for (FSMTransition t : map.keySet()) {
      if (map.get(t) > 0) {
        uncoveredTP.remove(t);
      }
    }
    log.debug("Uncovered TP:" + uncoveredTP);
    if (uncoveredTP.size() > 0) {
      return rand.oneOf(uncoveredTP);
    }
    
    return null;
  }

  /**
   * Updates the coverage table in memory based on the transition that has been chosen as the one to be taken.
   *
   * @param previous The previously taken transition.
   * @param choice   The choice of transition that will be taken next.
   */
  private void updateTPCoverage(String previous, FSMTransition choice) {
    if (previous == null) {
      return;
    }
    Map<FSMTransition, Integer> currentTpCoverage = tpCoverage.get(previous);
    int count = currentTpCoverage.get(choice);
    currentTpCoverage.put(choice, count + 1);
  }

  @Override
  public void initTest() {
  }
}
