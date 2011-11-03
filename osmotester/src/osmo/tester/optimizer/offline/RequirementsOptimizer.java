package osmo.tester.optimizer.offline;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Optimizes the given test suite so that most requirements are covered at the beginning of the test suite.
 *
 * @author Teemu Kanstren
 */
public class RequirementsOptimizer extends BaseOptimizer {
  private static Logger log = new Logger(RequirementsOptimizer.class);

  @Override
  public List<TestCase> optimize(TestSuite suite) {
    log.debug("Optimizing test suite for requirements coverage.");
    Collection<TestCase> tests = suite.getAllTestCases();
    Collection<String> totalCoverage = new ArrayList<String>();
    List<TestCase> result = new ArrayList<TestCase>();
    while (tests.size() > 0) {
      log.debug("Searching for the next test");
      TestCase next = findNext(tests, totalCoverage);
      log.debug("Next given:" + next);
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
   * Finds the next test case from the set of remaining ones, being the one that provides the most new requirements
   * coverage.
   *
   * @param tests         The set of tests where to find the next one.
   * @param totalCoverage The total coverage of requirements so far for all previous tests.
   * @return The next test case that adds most to the requirements coverage.
   */
  private TestCase findNext(Collection<TestCase> tests, Collection<String> totalCoverage) {
    TestCase next = null;
    Collection<String> maxTestCoverage = new HashSet<String>();
    for (TestCase test : tests) {
      List<TestStep> steps = test.getSteps();
      Collection<String> testCoverage = new HashSet<String>();
      for (TestStep step : steps) {
        Collection<String> stepCoverage = step.getCoveredRequirements();
        testCoverage.addAll(stepCoverage);
        stepCoverage.removeAll(totalCoverage);
      }
      if (testCoverage.size() > maxTestCoverage.size()) {
        maxTestCoverage = testCoverage;
        next = test;
      }
    }
    totalCoverage.addAll(maxTestCoverage);
    return next;
  }
}
