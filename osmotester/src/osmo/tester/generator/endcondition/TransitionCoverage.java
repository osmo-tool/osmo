package osmo.tester.generator.endcondition;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Ends the test/suite generation when a given percentage of all transitions in the model have been covered.
 * The required coverage can be 0-100% but also higher, meaning that for example 150% coverage means that
 * all transitions have been covered at least once and half of the transitions at least twice.
 *
 * @author Teemu Kanstren
 */
public class TransitionCoverage implements EndCondition {
  private static final Logger log = new Logger(TransitionCoverage.class);
  /** Required transition coverage. 1=100%, 1.5=150% and so on.*/
  private final double threshold;

  public TransitionCoverage(double threshold) {
    this.threshold = threshold;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return checkThreshold(suite, fsm);
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return checkThreshold(suite, fsm);
  }

  public boolean checkThreshold(TestSuite suite, FSM fsm) {
    double ratio = 0;
    Map<FSMTransition, Integer> coverage = suite.getTransitionCoverage();

    Collection<FSMTransition> temp = new ArrayList<FSMTransition>();
    Collection<FSMTransition> all = fsm.getTransitions();
    int allCount = all.size();
    temp.addAll(coverage.keySet());
    int min = 0;
    if (temp.containsAll(all)) {
      min = Integer.MAX_VALUE;
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

    log.debug("ratio:"+ratio + "threshold:"+threshold);
    return ratio >= threshold;
  }
}
