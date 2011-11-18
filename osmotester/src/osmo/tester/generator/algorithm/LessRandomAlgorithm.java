package osmo.tester.generator.algorithm;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static osmo.common.TestUtils.minOf;
import static osmo.common.TestUtils.oneOf;

/**
 * A test generation algorithm that is similar to the {@link RandomAlgorithm} but not preferring to take
 * a the least covered of all available transitions (randomly). The difference is that a single
 * transition is not taken many times until other available ones have been taken equally many times.
 * The same also applies to transition-pairs such that the next transition chosen is based on either the set
 * of completely uncovered transitions (with "null" source for a pair) and for a transition pair with the
 * previously taken transition as a source.
 * In summary the transition chosen is one where the source->destination transitions of a pair is one of the
 * least covered pairs. The source can be "null" meaning it is the first time ever the transition is taken.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class LessRandomAlgorithm implements FSMTraversalAlgorithm {
  private static Logger log = new Logger(LessRandomAlgorithm.class);
  /** The coverage for transitions pairs, key = source transition, value = {destination transition, coverage} */
  private Map<FSMTransition, Map<FSMTransition, Integer>> tpCoverage = new HashMap<FSMTransition, Map<FSMTransition, Integer>>();

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    Map<FSMTransition, Integer> tCoverage = history.getTransitionCoverage();
    TestStep ts = history.getCurrentTest().getCurrentStep();
    FSMTransition previous = null;
    if (ts != null) {
      previous = ts.getTransition();
    }

    //we use a hashset to avoid duplicates from different calculations
    Collection<FSMTransition> options = new HashSet<FSMTransition>();
    options.addAll(choices);
    options.removeAll(tCoverage.keySet());
    log.debug("uncovered options:" + options);
    //options now contains all previously uncovered transitions

    //we add also all previously uncovered transition pairs to options
    addUncoveredTP(previous, options, choices);

    Map<FSMTransition, Integer> currentTPCoverage = getTPCoverageFor(previous, choices);
    //if we have nothing left uncovered, we pick one based on least coverage
    if (options.size() == 0) {
      options.addAll(coverageBasedOptions(history, choices, currentTPCoverage));
    }

    FSMTransition choice = oneOf(options);
    updateTPCoverage(previous, choice);
    return choice;
  }

  /**
   * Picks one of the available choices based on which of the choices (transition) or their pairs (transition pairs)
   * have been covered the least. For example, half of the options and their pairs have been covered once and
   * the other half twice, then the choice is one of the half that has been covered once.
   * A pair is considered to form from the previously taken transition + the one that would be chosen now.
   * The set of taken transition pairs is available in the mapping this class holds over time and that is updated
   * every time a new transition is taken.
   *
   * @param history           The test generation history.
   * @param choices           Available transition choices in the current state.
   * @param currentTPCoverage Transition pair coverage from the previously taken transition.
   * @return An option based on coverage analysis of available options.
   */
  private Collection<FSMTransition> coverageBasedOptions(TestSuite history, List<FSMTransition> choices, Map<FSMTransition, Integer> currentTPCoverage) {
    Map<FSMTransition, Integer> tCoverage = history.getTransitionCoverage();

    Collection<FSMTransition> options = new HashSet<FSMTransition>();
    //we have covered everything at least once so lets count the coverage instead
    int smallest = minOf(tCoverage.values());
//    Map<FSMTransition, Integer> currentTPCoverage = checkTPCoverageFor(current, choices);
    int smallestTP = minOf(currentTPCoverage.values());
    log.debug("smallest:" + smallest + " stp:" + smallestTP);

    for (FSMTransition t : choices) {
      if (tCoverage.get(t) == smallest) {
        options.add(t);
      }
      if (currentTPCoverage.get(t) == smallestTP) {
        options.add(t);
      }
    }
    return options;
  }

  /**
   * Provides the transition pair coverage for the previously taken transition. That is, how many times other
   * transitions have been taken after the previously taken transition.
   * Also initializes the default value of 0 for any available transition that has not been taken, to make sure
   * that get() will always return something valid for the available choices.
   *
   * @param previous The transition that was taken previously (pair source).
   * @param choices  The choices for the next transition (pair destination).
   * @return Calculated coverage for the available source-destination pairs.
   */
  private Map<FSMTransition, Integer> getTPCoverageFor(FSMTransition previous, List<FSMTransition> choices) {
    Map<FSMTransition, Integer> currentTPCoverage = tpCoverage.get(previous);
    if (currentTPCoverage == null) {
      currentTPCoverage = new HashMap<FSMTransition, Integer>();
      tpCoverage.put(previous, currentTPCoverage);
    }
    for (FSMTransition t : choices) {
      if (currentTPCoverage.get(t) == null) {
        currentTPCoverage.put(t, 0);
      }
    }
    return currentTPCoverage;
  }

  /**
   * Adds any available and completely uncovered transition pairs to the given set of options.
   *
   * @param previous Previously taken transition (pair source).
   * @param options  Current choices, for example, already added from completely uncovered transitions (with "null" source).
   * @param choices  The possible choices for the next transition to be taken (pair destination).
   */
  private void addUncoveredTP(FSMTransition previous, Collection<FSMTransition> options, Collection<FSMTransition> choices) {
    Collection<FSMTransition> uncoveredTP = new ArrayList<FSMTransition>();
    uncoveredTP.addAll(choices);
    if (previous != null) {
      Map<FSMTransition, Integer> map = tpCoverage.get(previous);
      if (map == null) {
        map = new HashMap<FSMTransition, Integer>();
        tpCoverage.put(previous, map);
      }
      for (FSMTransition t : map.keySet()) {
        if (map.get(t) > 0) {
          uncoveredTP.remove(t);
        }
      }
    }
    log.debug("Uncovered TP:" + uncoveredTP);
    //options now contains all previously uncovered transition pairs
    //Make union
    options.addAll(uncoveredTP);
  }

  /**
   * Updates the coverage table in memory based on the transition that has been chosen as the one to be taken.
   *
   * @param previous The previously taken transition.
   * @param choice   The choice of transition that will be taken next.
   */
  private void updateTPCoverage(FSMTransition previous, FSMTransition choice) {
    if (previous == null) {
      return;
    }
    Map<FSMTransition, Integer> currentTpCoverage = tpCoverage.get(previous);
    int count = currentTpCoverage.get(choice);
    currentTpCoverage.put(choice, count + 1);
  }
}
