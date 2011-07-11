package osmo.tester.optimizer;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSMTransition;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Base class for test suite optimizers.
 *
 * @author Teemu Kanstren
 */
public abstract class BaseOptimizer {
  /**
   * Recalculates how may new requirements and transitions a test case covers as opposed to those listed
   * previously in the test suite (list of test cases). Note that this is dependent on the ordering of the
   * tests in the given list, if the ordering is changed, recalculation should be performed.
   *
   * @param tests The list of tests to calculate coverage for.
   */
  public void recalculateAddedCoverage(List<TestCase> tests) {
    Collection<String> coveredRequirements = new HashSet<String>();
    Collection<FSMTransition> coveredTransitions = new HashSet<FSMTransition>();
    for (TestCase test : tests) {
      test.resetCoverage();
      List<TestStep> steps = test.getSteps();
      for (TestStep step : steps) {
        Collection<String> reqs = step.getCoveredRequirements();
        reqs.removeAll(coveredRequirements);
        for (String req : reqs) {
          coveredRequirements.add(req);
          test.addAddedRequirementsCoverage(req);
        }
        FSMTransition transition = step.getTransition();
        if (!coveredTransitions.contains(transition)) {
          coveredTransitions.add(transition);
          test.addAddedTransitionCoverage(transition);
        }
      }
    }
  }

  /**
   * Subsclasses should implement this to optimize the ordering of the given test suite according to the chosen
   * optimization strategy.
   *
   * @param suite The test suite to be optimized.
   * @return The new ordering of test cases.
   */
  public abstract List<TestCase> optimize(TestSuite suite);
}
