package osmo.tester.generator.algorithm;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;
import osmo.tester.parser.ParserResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * A test generation algorithm that is similar to the {@link RandomAlgorithm} but prefers to take
 * a the least covered of all available steps (randomly). The difference is that a single
 * test step is not taken many times until other available ones have been taken equally many times.
 * The same also applies to step-pairs such that the next transition chosen is based on either the set
 * of completely uncovered transitions (with "null" source for a pair) and for a step pair with the
 * previously taken step as a source.
 * In summary the step chosen is one where the source->destination pair is one of the
 * least covered pairs. The source can be "null" meaning it is the first time ever the step is taken.
 *
 * @author Teemu Kanstren, Olli-Pekka Puolitaival
 */
public class BalancingAlgorithm implements FSMTraversalAlgorithm {
  private static Logger log = new Logger(BalancingAlgorithm.class);
  /** The coverage for transitions pairs, key = source transition, value = {destination transition, coverage} */
  private Map<String, Map<FSMTransition, Integer>> tpCoverage = new HashMap<>();
  /** For randomization. Separate instances are used to allow multiple instances running concurrently. */
  private final Randomizer rand;

  public BalancingAlgorithm() {
    this.rand = new Randomizer(OSMOConfiguration.getSeed());
  }

  @Override
  public void init(ParserResult parserResult) {
  }

  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> choices) {
    //how many times each transition has been taken so far
    Map<String, Integer> tCoverage = history.getTransitionCoverage();
    TestCaseStep ts = history.getCurrentTest().getCurrentStep();
    String previous = null;
    if (ts != null) {
      previous = ts.getName();
    }

    //we use a hashset to avoid duplicates from different calculations
    Collection<FSMTransition> options = new LinkedHashSet<>();
    options.addAll(choices);
    for (Iterator<FSMTransition> i = options.iterator() ; i.hasNext() ; ) {
      FSMTransition next = i.next();
      if (tCoverage.containsKey(next.getStringName())) {
        i.remove();
      }
    }
//    options.removeAll(tCoverage.keySet());
    log.debug("uncovered options:" + options);
    //options now contains all previously uncovered transitions

    //we add also all previously uncovered transition pairs to options
    addUncoveredTP(previous, options, choices);

    //calculate the transition pair coverage. needs to be done always to initialize the data structure for updating anyway
    Map<FSMTransition, Integer> currentTPCoverage = getTPCoverageFor(previous, choices);
    //if we have nothing left uncovered, we pick one based on least coverage
    if (options.size() == 0) {
      options.addAll(coverageBasedOptions(history, choices, currentTPCoverage));
    }

    //randomly pick one of the options
    FSMTransition choice = rand.oneOf(options);
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
    Map<String, Integer> tCoverage = history.getTransitionCoverage();

    Map<FSMTransition, Integer> choiceTC = new HashMap<>();
    for (FSMTransition choice : choices) {
      choiceTC.put(choice, tCoverage.get(choice.getStringName()));
    }

    Map<FSMTransition, Integer> choiceTPC = new HashMap<>();
    for (FSMTransition choice : choices) {
      choiceTPC.put(choice, currentTPCoverage.get(choice));
    }

    Collection<FSMTransition> options = new LinkedHashSet<>();

    //we have covered everything at least once so lets count the coverage instead
    int smallest = rand.minOf(choiceTC.values());
//    Map<FSMTransition, Integer> currentTPCoverage = checkTPCoverageFor(current, choices);
    int smallestTP = rand.minOf(choiceTPC.values());
    log.debug("smallest:" + smallest + " stp:" + smallestTP);
    log.debug("choices:" + choices);

    for (FSMTransition t : choices) {
      if (tCoverage.get(t.getStringName()) == smallest) {
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
  private Map<FSMTransition, Integer> getTPCoverageFor(String previous, List<FSMTransition> choices) {
    //this could be initialized once in init() but works so..
    Map<FSMTransition, Integer> currentTPCoverage = tpCoverage.get(previous);
    if (currentTPCoverage == null) {
      currentTPCoverage = new HashMap<>();
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
  private void addUncoveredTP(String previous, Collection<FSMTransition> options, Collection<FSMTransition> choices) {
    Collection<FSMTransition> uncoveredTP = new ArrayList<>();
    if (previous != null) {
      uncoveredTP.addAll(choices);
      Map<FSMTransition, Integer> map = tpCoverage.get(previous);
      if (map == null) {
        map = new HashMap<>();
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
