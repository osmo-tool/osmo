package osmo.tester.generator.filter;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A filter that defines the maximum number of times that a transition is allowed to be taken in a test case.
 *
 * @author Teemu Kanstren
 */
public class MaxTransitionFilter implements TransitionFilter {
  private static Logger log = new Logger(MaxTransitionFilter.class);
  /** Maximum allowed for transitions. Key = transition name, value = maximum number of times to take. */
  private Map<String, Integer> maximums = new HashMap<String, Integer>();
  /** The actual count of times a transition has been taken. Key = transition name, value = number of times taken. */
  private Map<String, Integer> taken = new HashMap<String, Integer>();

  /**
   * Define the maximum number of times a transition is allowed to be taken.
   *
   * @param transitionName The name of transition.
   * @param max            The maximum times it can be taken.
   */
  public void setMax(String transitionName, int max) {
    if (max < 0) {
      throw new IllegalArgumentException("Transition max count is now allowed to be negative. Was " + max + " for '" + transitionName + "'.");
    }
    maximums.put(transitionName, max);
  }

  /**
   * Filters out the transitions that have already reached their maximum allowed occurrences.
   *
   * @param transitions The set of transitions to be modified.
   */
  @Override
  public void filter(Collection<FSMTransition> transitions) {
    for (Iterator<FSMTransition> i = transitions.iterator(); i.hasNext(); ) {
      FSMTransition transition = i.next();
      String name = transition.getName();
      Integer count = taken.get(name);
      if (count == null) {
        count = 0;
      }
      Integer max = maximums.get(name);
      if (max == null) {
        continue;
      }
      if (count >= max) {
        log.debug("Removing transition '" + name + "' due to filtering.");
        i.remove();
      }
    }
  }

  @Override
  public void init(FSM fsm) {
    Collection<FSMTransition> transitions = fsm.getTransitions();
    Collection<String> shouldClear = new ArrayList<String>();
    shouldClear.addAll(maximums.keySet());
    for (FSMTransition transition : transitions) {
      String name = transition.getName();
      shouldClear.remove(name);
    }
    if (shouldClear.size() > 0) {
      throw new IllegalArgumentException("Specified transitions do not exist in the model:" + shouldClear);
    }
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void transition(FSMTransition transition) {
    String name = transition.getName();
    Integer count = taken.get(name);
    if (count == null) {
      count = 0;
    }
    count++;
    taken.put(name, count);
  }

  @Override
  public void pre(FSMTransition transition) {
  }

  @Override
  public void post(FSMTransition transition) {
  }

  /**
   * We have to clear the set of covered transitions for test case or the filter will be suite-level.
   *
   * @param test The associated test object.
   */
  @Override
  public void testStarted(TestCase test) {
    taken.clear();
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
}
