package osmo.tester.junit;

import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

/** @author Teemu Kanstren */
public class JUnitGenerationListener implements GenerationListener {
  private final int expected;
  private int actual = 0;
  private final MainGenerator generator;

  public JUnitGenerationListener(int expected, MainGenerator generator) {
    this.expected = expected;
    this.generator = generator;
  }

  @Override
  public void init(FSM fsm) {
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
  }

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
}
