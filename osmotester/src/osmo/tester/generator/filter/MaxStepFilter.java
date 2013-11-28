package osmo.tester.generator.filter;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A filter that removes test steps from available sets after the step is taken given number of times.
 *
 * @author Teemu Kanstren
 */
public class MaxStepFilter implements StepFilter {
  private static final Logger log = new Logger(MaxStepFilter.class);
  /** Maximum allowed step count. Key = step name, value = maximum number of times to take. */
  private Map<String, Integer> maximums = new HashMap<>();
  /** The actual count of step has been taken. Key = step name, value = number of times taken. */
  private Map<String, Integer> taken = new HashMap<>();

  /**
   * Define the maximum number of times a step is allowed to be taken.
   *
   * @param stepName The name of step.
   * @param max      The maximum times it can be taken.
   */
  public void setMax(String stepName, int max) {
    if (max < 0) {
      throw new IllegalArgumentException("Step max count is now allowed to be negative. Was " + max + " for '" + stepName + "'.");
    }
    maximums.put(stepName, max);
  }

  /**
   * Filters out the steps that have already reached their maximum allowed occurrences.
   *
   * @param steps The set of transitions to be modified.
   */
  @Override
  public void filter(Collection<FSMTransition> steps) {
    for (Iterator<FSMTransition> i = steps.iterator() ; i.hasNext() ; ) {
      FSMTransition transition = i.next();
      String name = transition.getStringName();
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
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
    Collection<FSMTransition> transitions = fsm.getTransitions();
    Collection<String> shouldClear = new ArrayList<>();
    shouldClear.addAll(maximums.keySet());
    for (FSMTransition transition : transitions) {
      String name = transition.getStringName();
      shouldClear.remove(name);
    }
    if (shouldClear.size() > 0) {
      throw new IllegalArgumentException("Specified steps do not exist in the model:" + shouldClear);
    }
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void step(TestCaseStep step) {
    String name = step.getName();
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
   * We have to clear the set of covered steps for test case or the filter will be suite-level.
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

  @Override
  public void testError(TestCase test, Throwable error) {
  }

  @Override
  public void lastStep(String name) {
  }
}
