package osmo.tester.scenario;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.filter.StepFilter;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A filter that removes test steps from available sets according to scenario slice definition.
 * Only applied after possible startup slice is done.
 *
 * @author Teemu Kanstren
 */
public class SliceFilter implements StepFilter {
  private static Logger log = new Logger(SliceFilter.class);
  private Collection<String> allowed = new HashSet<>();
  /** Maximum allowed step count. Key = step name, value = maximum number of times to take. */
  private Map<String, Integer> maximums = new HashMap<>();
  /** The actual count of step has been taken. Key = step name, value = number of times taken. */
  private Map<String, Integer> taken = new HashMap<>();
  private final Scenario scenario;
  private final StartupFilter startup;

  public SliceFilter(Scenario scenario, StartupFilter startup) {
    this.scenario = scenario;
    this.startup = startup;
    List<Slice> slices = scenario.getSlices();
    for (Slice slice : slices) {
      if (scenario.isStrict()) allowed.add(slice.getStepName());
      if (slice.getMax() > 0) {
        maximums.put(slice.getStepName(), slice.getMax());
      }
    }
  }

  /**
   * Filters out the steps that have already reached their maximum allowed occurrences.
   *
   * @param steps The set of transitions to be modified.
   */
  @Override
  public void filter(Collection<FSMTransition> steps) {
    if (!startup.isDone()) return;
    for (Iterator<FSMTransition> i = steps.iterator() ; i.hasNext() ; ) {
      FSMTransition transition = i.next();
      String name = transition.getStringName();
      if (allowed.size() > 0 && !allowed.contains(name)) {
        i.remove();
        continue;
      }
      Integer max = maximums.get(name);
      if (max == null) {
        continue;
      }
      Integer count = taken.get(name);
      if (count == null) {
        count = 0;
      }
      if (count >= max) {
        log.debug("Removing transition '" + name + "' due to filtering.");
        i.remove();
      }
    }
  }

  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
    //do not check feasibility here as the scenario should already handle it
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void step(TestCaseStep step) {
    if (step.getParent().getSteps().size() <= scenario.getStartup().size()) return;
    //TODO: how does exploration affect this? how does this affect exploration (as it should)?
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
}
