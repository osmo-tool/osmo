package osmo.tester.gui.jfx.executiontab;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.gui.jfx.executiontab.single.BasicExecutorPane;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

/**
 * @author Teemu Kanstren
 */
public class CoverageListener implements GenerationListener {
  private final BasicExecutorPane pane;

  public CoverageListener(BasicExecutorPane pane) {
    this.pane = pane;
  }

  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
    
  }

  @Override
  public void guard(FSMTransition transition) {

  }

  @Override
  public void step(TestCaseStep step) {

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
    pane.testEnded(test);
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
