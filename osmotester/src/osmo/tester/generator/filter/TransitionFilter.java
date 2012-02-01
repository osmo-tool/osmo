package osmo.tester.generator.filter;

import osmo.tester.generator.GenerationListener;
import osmo.tester.model.FSMTransition;

import java.util.Collection;

/**
 * An interface for filters that remove from a given set of transitions those that should not be taken at this point
 * (according to the given filter).
 *
 * @author Teemu Kanstren
 */
public interface TransitionFilter extends GenerationListener {
  /**
   * Filter a given set of transitions, removing ones that are now allowed by this filter at this time.
   *
   * @param transitions The set of transitions to be modified.
   */
  public void filter(Collection<FSMTransition> transitions);
}
