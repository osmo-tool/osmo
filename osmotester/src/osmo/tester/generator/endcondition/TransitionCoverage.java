package osmo.tester.generator.endcondition;

import osmo.common.log.Logger;
import osmo.tester.generator.testsuite.TestStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ends the test/suite generation when a given percentage of all transitions in the model have been covered.
 * The required coverage can be 0-100% but also higher, meaning that for example 150% coverage means that
 * all transitions have been covered at least once and half of the transitions at least twice.
 *
 * @author Teemu Kanstren
 */
public class TransitionCoverage extends AbstractEndCondition {
  private static final Logger log = new Logger(TransitionCoverage.class);
  /** Required transition coverage. 1=100%, 1.5=150% and so on. */
  private final double threshold;

  /**
   * Constructor.
   *
   * @param threshold The coverage threshold.
   */
  public TransitionCoverage(double threshold) {
    this.threshold = threshold;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return checkThreshold(suite, fsm, true);
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return checkThreshold(suite, fsm, false);
  }

  public boolean checkThreshold(TestSuite suite, FSM fsm, boolean suiteCheck) {
    double ratio = 0;

    Map<FSMTransition, Integer> coverage = suite.getTransitionCoverage();
    if (!suiteCheck) {
      coverage = new HashMap<FSMTransition, Integer>();
      List<TestStep> steps = suite.getCurrentTest().getSteps();
      for (TestStep step : steps) {
        FSMTransition t = step.getTransition();
        Integer count = coverage.get(t);
        if (count == null) {
          count = 0;
        }
        coverage.put(t, count + 1);
      }
    }
    Collection<FSMTransition> temp = new ArrayList<FSMTransition>();
    Collection<FSMTransition> all = fsm.getTransitions();
    int allCount = all.size();
    temp.addAll(coverage.keySet());
    if (temp.containsAll(all)) {
      int min = Integer.MAX_VALUE;
      for (FSMTransition transition : temp) {
        int count = coverage.get(transition);
        if (count < min) {
          min = count;
        }
      }
      int partials = 0;
      for (FSMTransition transition : temp) {
        int count = coverage.get(transition);
        if (count > min) {
          partials++;
        }
      }
      ratio += min;
      ratio += ((double) partials) / ((double) allCount);
    } else {
      int coveredCount = temp.size();
      ratio = ((double) coveredCount) / ((double) allCount);
    }

/*    while (true) {
      if (temp.containsAll(all)) {
        for (FSMTransition t : all) {
          temp.remove(t);
        }
        ratio += 1;
      } else {
        break;
      }
    }*/

    log.debug("ratio:" + ratio + "threshold:" + threshold);
    return ratio >= threshold;
  }

  @Override
  public void init(FSM fsm) {
  }
}
