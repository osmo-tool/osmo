package osmo.tester.generator.algorithm;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static osmo.tester.TestUtils.*;

/**
 * A test generation algorithm that is similar to the {@link RandomAlgorithm} but not preferring to take
 * a the least covered of all available transitions (randomly). The difference is that a single
 * transition is not taken many times until other available ones have been taken equally many times.
 *
 * @author Teemu Kanstren
 */
public class OptimizedRandomAlgorithm implements FSMTraversalAlgorithm {
  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> transitions) {
    Map<FSMTransition, Integer> coverage = countCoverage(history);
    return optimizedRandomChoice(coverage, transitions);
  }

  /**
   * Picks one from the set of available choices according to the optimized random algorithm.
   * This is static and generalized to allow use from various algorithms such as transition traversal choice
   * or data generator.
   *
   * @param coverage How many times each item has been covered. Input for the algorithm to choose the least covered.
   * @param choices The set of possible choices where to pick the next from.
   * @param <T> The type (class) of choices.
   * @return A choice picked according to the optimized random algorithm.
   */
  public static <T> T optimizedRandomChoice(Map<T, Integer> coverage, Collection<T> choices) {
    Collection<T> options = new ArrayList<T>();
    options.addAll(choices);
    options.removeAll(coverage.keySet());
    //options now contains all previously uncovered transitions
    if (options.size() == 0) {
      //we have covered everything at least once so lets count the coverage instead
      int smallest = minOf(coverage.values());
      for (T t : choices) {
        if (coverage.get(t) == smallest) {
          options.add(t);
        }
      }
      if (options.size() == 0) {
        options = choices;
      }
    }
    return oneOf(options);
  }

  /**
   * Counts how many times different transitions have been taken in the overall test generation
   * history. This counds as coverage for that transition.
   *
   * @param history The test suite generated (test history).
   * @return Number of times each transition has been taken.
   */
  private Map<FSMTransition, Integer> countCoverage(TestSuite history) {
    Map<FSMTransition, Integer> coverage = new HashMap<FSMTransition, Integer>();
    List<TestCase> tests = history.getAllTestCases();
    for (TestCase test : tests) {
      List<TestStep> steps = test.getSteps();
      for (TestStep step : steps) {
        FSMTransition transition = step.getTransition();
        Integer count = coverage.get(transition);
        if (count == null) {
          count = 0;
        }
        coverage.put(transition, count+1);
      }
    }
    return coverage;
  }
}
