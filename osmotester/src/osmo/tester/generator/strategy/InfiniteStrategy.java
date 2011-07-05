package osmo.tester.generator.strategy;

import osmo.tester.generator.testsuite.TestSuite;

/**
 * This causes test generation to go on basically forever. Only way to stop the test case/test suite generation
 * when using this strategy is to provide an {@link osmo.tester.annotation.EndCondition} yourself.
 *
 * @author Teemu Kanstren
 */
public class InfiniteStrategy implements ExitStrategy {
  @Override
  public boolean exitNow(TestSuite testLog, boolean evaluateSuite) {
    return false;
  }
}
