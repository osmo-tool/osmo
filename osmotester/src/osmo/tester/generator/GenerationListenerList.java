package osmo.tester.generator;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Maintains a list of {@link GenerationListener} implementations to be called.
 *
 * @author Teemu Kanstren
 */
public class GenerationListenerList implements GenerationListener {
  /** The list of listeners to be invoked. */
  private Collection<GenerationListener> listeners = new ArrayList<GenerationListener>();

  /**
   * Add a new listener to be invoked.
   *
   * @param listener The listener to add.
   */
  public void addListener(GenerationListener listener) {
    listeners.add(listener);
  }

  @Override
  public void init(FSM fsm) {
    for (GenerationListener listener : listeners) {
      listener.init(fsm);
    }
  }

  @Override
  public void guard(FSMTransition transition) {
    for (GenerationListener listener : listeners) {
      listener.guard(transition);
    }
  }

  @Override
  public void transition(FSMTransition transition) {
    for (GenerationListener listener : listeners) {
      listener.transition(transition);
    }
  }

  @Override
  public void pre(FSMTransition transition) {
    for (GenerationListener listener : listeners) {
      listener.pre(transition);
    }
  }

  @Override
  public void post(FSMTransition transition) {
    for (GenerationListener listener : listeners) {
      listener.post(transition);
    }
  }

  @Override
  public void testStarted(TestCase test) {
    for (GenerationListener listener : listeners) {
      listener.testStarted(test);
    }
  }

  @Override
  public void testEnded(TestCase test) {
    for (GenerationListener listener : listeners) {
      listener.testEnded(test);
    }
  }

  @Override
  public void suiteStarted(TestSuite suite) {
    for (GenerationListener listener : listeners) {
      listener.suiteStarted(suite);
    }
  }

  @Override
  public void suiteEnded(TestSuite suite) {
    for (GenerationListener listener : listeners) {
      listener.suiteEnded(suite);
    }
  }
}
