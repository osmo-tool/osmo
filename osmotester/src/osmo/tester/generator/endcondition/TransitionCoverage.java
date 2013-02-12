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

  /**
   * Checks if the defined transition coverage has been achieved for test case or test suite.
   *
   * @param suite      The generated suite so far, including the current test being generated.
   * @param fsm        The model.
   * @param suiteCheck If true, this is a check for test suite, otherwise for a test case.
   * @return True if requested coverage has been achieved.
   */
  public boolean checkThreshold(TestSuite suite, FSM fsm, boolean suiteCheck) {
    Map<String, Integer> coverage = suite.getTransitionCoverage();
    if (!suiteCheck) {
      //it is a test check
      coverage = new HashMap<>();
      List<TestStep> steps = suite.getCurrentTest().getSteps();
      for (TestStep step : steps) {
        String t = step.getName();
        Integer count = coverage.get(t);
        if (count == null) {
          count = 0;
        }
        coverage.put(t, count + 1);
      }
    }
    Collection<String> temp = new ArrayList<>();
    Collection<String> all = new ArrayList<>();
    for (FSMTransition transition : fsm.getTransitions()) {
      all.add(transition.getStringName());
    }
//    Collection<FSMTransition> all = fsm.getTransitions();
    int allCount = all.size();
    temp.addAll(coverage.keySet());
    double ratio = 0;
    if (temp.containsAll(all)) {
      //we come here if min of 100% coverage is achieved
      int min = Integer.MAX_VALUE;
      //find the one(s) with the least coverage
      for (String transition : temp) {
        int count = coverage.get(transition);
        if (count < min) {
          min = count;
        }
      }
      log.debug("least times a transition was covered = " + min);
      int partials = 0;
      //find how many are covered more than the minimum coverage
      for (String transition : temp) {
        int count = coverage.get(transition);
        if (count > min) {
          partials++;
        }
      }
      //if min coverage is one, it is added to make it 100%, similarly 2=200%, and so on
      ratio += min;
      ratio += ((double) partials) / ((double) allCount);
    } else {
      int coveredCount = temp.size();
      ratio = ((double) coveredCount) / ((double) allCount);
    }

    log.debug("ratio:" + ratio + "threshold:" + threshold);
    return ratio >= threshold;
  }

  @Override
  public void init(FSM fsm) {
  }

  @Override
  public String toString() {
    return "TransitionCoverage{" +
            "threshold=" + threshold +
            '}';
  }
}
