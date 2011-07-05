package osmo.tester.generator.algorithm;

import osmo.tester.HashMapWithDefaultValue;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static osmo.tester.TestUtils.oneOf;

/**
 * A test generation algorithm that is similar to the {@link RandomAlgorithm} but not preferring to take
 * a the least covered of all available transitions (randomly). The difference is that a single
 * transition is not taken many times until other available ones have been taken equally many times.
 *
 * @author Teemu Kanstren
 */
public class OptimizedRandomAlgorithm implements GenerationAlgorithm {
  @Override
  public FSMTransition choose(TestSuite history, List<FSMTransition> transitions) {
    Map<FSMTransition, Integer> coverage = countCoverage(history);
    int smallest = Integer.MAX_VALUE;
    for (FSMTransition transition : transitions) {
      Integer count = coverage.get(transition);
      if (count < smallest) {
        smallest = count;
      }
    }
    Collection<FSMTransition> options = new ArrayList<FSMTransition>();
    for (FSMTransition transition : transitions) {
      if (coverage.get(transition) == smallest) {
        options.add(transition);
      }
    }
    if (options.size() == 0) {
      options = transitions;
    }
    return oneOf(options);
  }

  private Map<FSMTransition, Integer> countCoverage(TestSuite history) {
    Map<FSMTransition, Integer> coverage = new HashMapWithDefaultValue<FSMTransition, Integer>(0);
    List<TestCase> tests = history.getAll();
    for (TestCase test : tests) {
      List<TestStep> steps = test.getSteps();
      for (TestStep step : steps) {
        FSMTransition transition = step.getTransition();
        Integer count = coverage.get(transition);
        coverage.put(transition, count+1);
      }
    }
    return coverage;
  }
}
