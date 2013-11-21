package osmo.tester.explorer;

import osmo.common.log.Logger;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;

/**
 * A task to be executed in a thread pool for calculating the added coverage score for given test case with regards
 * to the given existing suite coverage.
 *
 * @author Teemu Kanstren
 */
public class CoverageTask implements Runnable {
  private static final Logger log = new Logger(CoverageTask.class);
  /** The test case to calculate the score for. */
  private final TestCase test;
  /** The coverage so far for the test suite. The added new test coverage is in relation to this. */
  private final TestCoverage suiteCoverage;
  /** How many steps do we calculate the added coverage for in the given test case? */
  private final int numberOfSteps;
  /** Used to calculate the coverage score. */
  private final ScoreCalculator scoreCalculator;
  /** We use this key to hide the results in the TestCase attributes. */
  public static final String KEY = "osmo_test_added_coverage";

  public CoverageTask(TestCase test, int numberOfSteps, TestCoverage suiteCoverage, ScoreCalculator scoreCalculator) {
    this.test = test;
    this.numberOfSteps = numberOfSteps;
    this.suiteCoverage = suiteCoverage;
    this.scoreCalculator = scoreCalculator;
  }

  @Override
  public void run() {
//    int added = scoreCalculator.addedScoreFor(suiteCoverage, test, numberOfSteps);
    int added = scoreCalculator.addedScoreFor(suiteCoverage, test);
    log.debug("running coverage calculation task for " + numberOfSteps + " steps in " + test+" = "+added);
    test.setAttribute(KEY, added);
  }

}
