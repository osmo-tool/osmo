package osmo.tester.scenario;

import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.filter.StepFilter;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.scenario.Scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Attempts to force test generation to start with a given set of steps.
 * If a given step is not available at a given point (a guard is false), an exception is thrown.
 * 
 * @author Teemu Kanstren 
 */
public class StartupFilter implements StepFilter {
  /** The sequence of steps to take. */
  private List<String> steps = new ArrayList<>();
  /** Current index in the list of steps. */
  private int index = 0;
  /** When finished. */
  private boolean done = false;

  public StartupFilter(Scenario scenario) {
    this.steps = scenario.getStartup();
  }
  
  public boolean isDone() {
    return done;
  }

  @Override
  public void filter(Collection<FSMTransition> transitions) {
    if (done) return;
    String step = steps.get(index);
    for (Iterator<FSMTransition> i = transitions.iterator() ; i.hasNext() ; ) {
      FSMTransition transition = i.next();
      String name = transition.getStringName();
      //remove all but the one at the current index, forcing the selection of the only one remaining
      if (!name.equals(step)) i.remove();
    }
  }

  @Override
  public void init(long seed, FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public void guard(FSMTransition transition) {
  }

  @Override
  public void step(TestCaseStep step) {
    index++;
    if (index >= steps.size()) done = true;
  }

  @Override
  public void pre(FSMTransition transition) {
  }

  @Override
  public void post(FSMTransition transition) {
  }

  @Override
  public void testStarted(TestCase test) {
    //reset the filter for a new test case
    index = 0;
    done = false;
    //except if there is no startup sequence given
    if (steps.size() == 0) done = true;
  }

  @Override
  public void testEnded(TestCase test) {
  }

  @Override
  public void testError(TestCase test, Throwable error) {
  }

  @Override
  public void suiteStarted(TestSuite suite) {
  }

  @Override
  public void suiteEnded(TestSuite suite) {
  }
}
