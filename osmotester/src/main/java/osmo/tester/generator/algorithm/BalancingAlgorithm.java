package osmo.tester.generator.algorithm;

import osmo.common.Randomizer;
import osmo.common.Logger;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * A test generation algorithm that is similar to the {@link RandomAlgorithm} but prefers to take
 * a the least covered of all available steps. Any uncovered steps are taken first, and after this
 * any uncovered pairs are taken. If all available steps and pairs have been taken already, the
 * algorithm picks one randomly but giving higher weight to those that have been covered fewer times.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class BalancingAlgorithm implements FSMTraversalAlgorithm {
  private static final Logger log = new Logger(BalancingAlgorithm.class);
  /** The coverage for step pairs, key = step 1 in pair, value = {step 2 in pair, coverage} */
  private Map<String, Map<FSMTransition, Integer>> spCoverage = new LinkedHashMap<>();
  /** For randomization. Separate instances are used to allow multiple instances running concurrently. */
  private Randomizer rand = null;

  public BalancingAlgorithm() {
  }

  @Override
  public void init(long seed, FSM fsm) {
  }

  @Override
  public FSMTransition choose(TestSuite suite, List<FSMTransition> choices) {
    TestCaseStep ts = suite.getCurrentTest().getCurrentStep();
    String previous = null;
    if (ts != null) {
      previous = ts.getName();
    } else {
      previous = FSM.START_STEP_NAME;
    }
    initSPCoverage(previous, choices);

    FSMTransition choice = checkUncoveredSteps(suite, choices);
    if (choice != null) {
      updateSPCoverage(previous, choice);
      return choice;
    }

    choice = checkUncoveredSP(previous, choices);
    if (choice != null) {
      updateSPCoverage(previous, choice);
      return choice;
    }

    choice = weightedChoice(previous, choices);
    updateSPCoverage(previous, choice);
    
    return choice;
  }

  /**
   * Performs a weighted choice from available test steps, based on how often each of them
   * has occurred after the step previously taken in current test case.
   * 
   * @param previous The name of step previously taken in this test case.
   * @param choices The available steps in current model state.
   * @return The chosen step to be taken next.
   */
  private FSMTransition weightedChoice(String previous, List<FSMTransition> choices) {
    Map<FSMTransition, Integer> map = spCoverage.get(previous);
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
    //->if we had zero values for weights, those steps would never be taken
    max++;
    //invert the weights, that is the more often a step is taken in proportion to the other available steps, 
    //the smaller probability it should have to be taken next
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

  /**
   * Check the given choices of test steps if any exist that have never been taken at all (regardless of previous step).
   * If any are found, one of those is chosen randomly as the next one to take.
   * 
   * @param suite The test suite so far (all steps taken...).
   * @param choices The available steps to choose from.
   * @return The step to take. Null if no uncovered step is found.
   */
  private FSMTransition checkUncoveredSteps(TestSuite suite, List<FSMTransition> choices) {
    //how many times each step has been taken so far
    TestCoverage tc = new TestCoverage();
    tc.addCoverage(suite.getCoverage());
    tc.addCoverage(suite.getCurrentTest().getCoverage());
    Map<String, Integer> coverage = tc.getStepCoverage();
    Collection<FSMTransition> options = new LinkedHashSet<>();
    options.addAll(choices);
    for (Iterator<FSMTransition> i = options.iterator() ; i.hasNext() ; ) {
      FSMTransition next = i.next();
      if (coverage.containsKey(next.getStringName())) {
        //it was covered..
        i.remove();
      }
    }
    log.d("uncovered options:" + options);
    //options now contains all previously uncovered transitions
    if (options.size() > 0) {
      return rand.oneOf(options);
    }
    return null;
  }

  /**
   * Provides the step pair coverage for the previously taken step. That is, how many times other
   * steps have been taken after the one previously taken in current test case.
   * Also initializes the default value of 0 for any available step that has not been taken, to make sure
   * that get() will always return something valid for the available choices.
   *
   * @param previous The step that was taken previously (first step in pair).
   * @param choices  The choices for the next transition (second (latter) step in pair).
   */
  private void initSPCoverage(String previous, List<FSMTransition> choices) {
    //this could be initialized once in init() but works so..
    Map<FSMTransition, Integer> currentSPCoverage = spCoverage.get(previous);
    if (currentSPCoverage == null) {
      currentSPCoverage = new LinkedHashMap<>();
      spCoverage.put(previous, currentSPCoverage);
    }
    for (FSMTransition t : choices) {
      if (currentSPCoverage.get(t) == null) {
        currentSPCoverage.put(t, 0);
      }
    }
  }

  /**
   * Provides a list of all previously uncovered pairs.
   *
   * @param previous Previously taken step (first step in pair).
   * @param choices  The possible choices for the next step to be taken (second (later) step in pair).
   */
  private FSMTransition checkUncoveredSP(String previous, Collection<FSMTransition> choices) {
    Collection<FSMTransition> uncoveredSP = new ArrayList<>();
    uncoveredSP.addAll(choices);
    Map<FSMTransition, Integer> map = spCoverage.get(previous);
    for (FSMTransition t : map.keySet()) {
      if (map.get(t) > 0) {
        uncoveredSP.remove(t);
      }
    }
    log.d("Uncovered SP:" + uncoveredSP);
    if (uncoveredSP.size() > 0) {
      return rand.oneOf(uncoveredSP);
    }
    return null;
  }

  /**
   * Updates the coverage table based on the step that has been chosen as the one to be taken.
   *
   * @param previous The previously taken step.
   * @param choice   The choice of step that will be taken next.
   */
  private void updateSPCoverage(String previous, FSMTransition choice) {
    if (previous == null) {
      return;
    }
    Map<FSMTransition, Integer> currentSpCoverage = spCoverage.get(previous);
    int count = currentSpCoverage.get(choice);
    currentSpCoverage.put(choice, count + 1);
  }

  @Override
  public void initTest(long seed) {
    this.rand = new Randomizer(seed);
  }

  @Override
  public FSMTraversalAlgorithm cloneMe() {
    BalancingAlgorithm clone = new BalancingAlgorithm();
    return clone;
  }
}
