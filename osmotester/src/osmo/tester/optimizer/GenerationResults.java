package osmo.tester.optimizer;

import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;

import java.util.List;

/** @author Teemu Kanstren */
public class GenerationResults {
  private final TestCoverage coverage;
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
