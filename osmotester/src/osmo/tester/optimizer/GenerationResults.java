package osmo.tester.optimizer;

import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;

import java.util.List;

/** 
 * Results for a greedy search, including suite coverage and tests in suite.
 * 
 * @author Teemu Kanstren 
 */
public class GenerationResults {
  /** Coverage of test suite. */
  private final TestCoverage coverage;
  /** The test 'suite'. */
  private final List<TestCase> tests;

  public GenerationResults(List<TestCase> tests) {
    this.tests = tests;
    this.coverage = new TestCoverage(tests);
  }

  public TestCoverage getCoverage() {
    return coverage;
  }

  public List<TestCase> getTests() {
    return tests;
  }
}
