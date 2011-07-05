package osmo.tester.generator.strategy;

import osmo.tester.generator.testsuite.TestSuite;

/**
 * This interface defines the strategy for when test suite or test case generation is stopped.
 * 
 * @author Teemu Kanstren
 */
public interface ExitStrategy {
  /**
   * This method is called by the test generator to evaluate when to stop generating new steps for a test case
   * or new test cases for a test suite.
   *
   * @param testLog The set of previously generated tests, the current test and their generated steps.
   * @param evaluateSuite False if evaluating the stopping of generation steps for a single test case,
   *                   true if evaluating the stopping of test case generation for a test suite.
   * @return True to stop generation, false to continue.
   */
  public boolean exitNow(TestSuite testLog, boolean evaluateSuite);
}
