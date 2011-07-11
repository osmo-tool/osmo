package osmo.tester.optimizer;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Optimizes the given test suite so that most transitions are covered at the beginning of the test suite.

 * @author Teemu Kanstren
 */
public class TransitionOptimizer extends BaseOptimizer {
  @Override
  public List<TestCase> optimize(TestSuite suite) {
    Collection<TestCase> tests = suite.getAllTestCases();
    Collection<FSMTransition> totalCoverage = new ArrayList<FSMTransition>();
    List<TestCase> result = new ArrayList<TestCase>();
    while (tests.size() > 0) {
      TestCase next = findNext(tests, totalCoverage);
      if (next == null) {
        //the rest of the tests have no requirements coverage, so we add them all
        result.addAll(tests);
        tests.clear();
      } else {
        result.add(next);
        tests.remove(next);
      }
    }
    recalculateAddedCoverage(result);
    return result;
  }

  /**
   * Finds the next test case from the set of remaining ones, being the one that provides the most new transition
   * coverage.
   *
   * @param tests The set of tests where to find the next one.
   * @param totalCoverage The total coverage of transitions so far for all previous tests.
   * @return The next test case that adds most to the transition coverage.
   */
  private TestCase findNext(Collection<TestCase> tests, Collection<FSMTransition> totalCoverage) {
    TestCase next = null;
    Collection<FSMTransition> maxTestTransitions = new HashSet<FSMTransition>();
    for (TestCase test : tests) {
      List<TestStep> steps = test.getSteps();
      Collection<FSMTransition> testTransitions = new HashSet<FSMTransition>();
      for (TestStep step : steps) {
        FSMTransition transition = step.getTransition();
        if (!totalCoverage.contains(transition)) {
          testTransitions.add(transition);
        }
      }
      if (testTransitions.size() > maxTestTransitions.size()) {
        maxTestTransitions = testTransitions;
        next = test;
      }
    }
    totalCoverage.addAll(maxTestTransitions);
    return next;
  }
}
