package osmo.tester.gui.manualdrive;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

/** @author Teemu Kanstren */
public class GUIGenerationListener implements GenerationListener {
  private final ManualAlgorithm driver;

  public GUIGenerationListener(ManualAlgorithm driver) {
    this.driver = driver;
  }

  @Override
  public void init(FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void transition(FSMTransition transition) {
  }

  @Override
  public void pre(FSMTransition transition) {
  }

  @Override
  public void post(FSMTransition transition) {
  }

  @Override
  public void testStarted(TestCase test) {
    driver.testStarted();
  }

  @Override
  public void testEnded(TestCase test) {
    driver.testEnded();
  }

  @Override
  public void testError(TestCase test, Exception error) {
  }

  @Override
  public void suiteStarted(TestSuite suite) {
  }

  @Override
  public void suiteEnded(TestSuite suite) {
    driver.suiteEnded();
  }
}
