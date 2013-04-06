package osmo.tester.generator;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.io.PrintStream;

/** @author Teemu Kanstren */
public class TracePrinter implements GenerationListener {
  private PrintStream out = System.out;
  
  @Override
  public void init(FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void step(TestStep step) {
    String name = step.getName();
    out.println(name.toUpperCase());
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
  public void testError(TestCase test, Exception error) {
  }

  @Override
  public void suiteStarted(TestSuite suite) {
  }

  @Override
  public void suiteEnded(TestSuite suite) {
  }
}
