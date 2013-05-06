package osmo.tester.reporting.junit;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

/**
 * Used internally by OSMO Tester to make sure suite ending is handled properly.
 * This mainly means ensuring {@link osmo.tester.annotation.AfterSuite} methods and similar
 * teardown functionality is properly invoked.
 * Needs a separate listener since JUnit does not seem to support this type of functionality nicely.
 *
 * @author Teemu Kanstren
 */
public class JUnitGenerationListener implements GenerationListener {
  /** The expected number of tests to be generated. */
  private final int expected;
  /** The actual number of tests generated so far. */
  private int actual = 0;
  /** The generator used for generating the tests, and for running the teardown code when done. */
  private final MainGenerator generator;

  public JUnitGenerationListener(int expected, MainGenerator generator) {
    this.expected = expected;
    this.generator = generator;
  }

  @Override
  public void init(FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void step(TestStep step) {
  }

  @Override
  public void pre(FSMTransition transition) {
  }

  @Override
  public void post(FSMTransition transition) {
  }

  @Override
  public void testStarted(TestCase test) {
  }

  /**
   * Here is whre we keep track of the number of generated tests.
   *
   * @param test The associated test object.
   */
  @Override
  public void testEnded(TestCase test) {
    actual++;
    if (actual == expected) {
      generator.endSuite();
    }
  }

  @Override
  public void suiteStarted(TestSuite suite) {
  }

  @Override
  public void suiteEnded(TestSuite suite) {
  }

  @Override
  public void testError(TestCase test, Throwable error) {
  }
}
