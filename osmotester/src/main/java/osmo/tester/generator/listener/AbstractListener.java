package osmo.tester.generator.listener;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

/**
 * To help implement the {@link osmo.tester.generator.listener.GenerationListener} interface with less hassle.
 *
 * @author Teemu Kanstren
 */
public abstract class AbstractListener implements GenerationListener {
  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void stepStarting(TestCaseStep step) {

  }

  @Override
  public void stepDone(TestCaseStep step) {
  }

  @Override
  public void lastStep(String name) {
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

  @Override
  public void testEnded(TestCase test) {
  }

  @Override
  public void testError(TestCase test, Throwable error) {
  }

  @Override
  public void suiteStarted(TestSuite suite) {
  }

  @Override
  public void suiteEnded(TestSuite suite) {
  }
}
