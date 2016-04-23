package osmo.tester.generator.filter;

import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.model.FSMTransition;

import java.util.Collection;

/**
 * An interface for filters that remove from a given set of steps those that should not be taken at this point
 * (according to the filter).
 *
 * @author Teemu Kanstren
 */
public interface StepFilter extends GenerationListener {
  /**
   * Filter a given set of steps, removing ones that are now allowed by this filter at this time.
   *
   * @param transitions The set of potential steps to be modified.
   */
  public void filter(Collection<FSMTransition> transitions);
}
