package osmo.tester.generator;

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
  public void guard(String name) {
    for (GenerationListener listener : listeners) {
      listener.guard(name);
    }
  }

  @Override
  public void transition(String name) {
    for (GenerationListener listener : listeners) {
      listener.transition(name);
    }
  }

  @Override
  public void oracle(String name) {
    for (GenerationListener listener : listeners) {
      listener.oracle(name);
    }
  }

  @Override
  public void testStarted() {
    for (GenerationListener listener : listeners) {
      listener.testStarted();
    }
  }

  @Override
  public void testEnded() {
    for (GenerationListener listener : listeners) {
      listener.testEnded();
    }
  }

  @Override
  public void suiteStarted() {
    for (GenerationListener listener : listeners) {
      listener.suiteStarted();
    }
  }

  @Override
  public void suiteEnded() {
    for (GenerationListener listener : listeners) {
      listener.suiteEnded();
    }
  }
}
