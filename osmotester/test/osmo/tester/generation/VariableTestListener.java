package osmo.tester.generation;

import osmo.tester.OSMOConfiguration;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Pre;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class VariableTestListener implements GenerationListener {
  private Collection<String> variables = new ArrayList<>();

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
    Set<String> names = transition.getPrePostParameter().keySet();
    variables.addAll(names);
  }

  @Override
  public void post(FSMTransition transition) {
    Set<String> names = transition.getPrePostParameter().keySet();
    for (String name : names) {
      if (name.startsWith("old")) {
        continue;
      }
      assertTrue("same state should be available in @" + Post.class.getSimpleName() + " as was in @" + Pre.class.getName(), variables.contains(name));
    }
  }

  @Override
  public void testStarted(TestCase test) {
  }

  @Override
  public void testEnded(TestCase test) {
  }

  @Override
  public void suiteStarted(TestSuite suite) {
  }

  @Override
  public void suiteEnded(TestSuite suite) {
  }

  @Override
  public void testError(TestCase test, Exception error) {
  }
}
