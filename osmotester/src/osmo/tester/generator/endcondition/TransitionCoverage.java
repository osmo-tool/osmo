package osmo.tester.generator.endcondition;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class TransitionCoverage implements EndCondition {
  private final double threshold;

  public TransitionCoverage(double threshold) {
    this.threshold = threshold;
  }

  @Override
  public boolean endSuite(TestSuite suite, FSM fsm) {
    return checkThreshold(suite, fsm);
  }

  public static void main(String[] args) {
    int allCount = 22;
    int uncoveredCount = 16;
    int coveredCount = allCount - uncoveredCount;
    double ratio = ((double)coveredCount) / ((double)allCount);
    System.out.println("ratio:"+ratio);
  }

  @Override
  public boolean endTest(TestSuite suite, FSM fsm) {
    return checkThreshold(suite, fsm);
  }

  public boolean checkThreshold(TestSuite suite, FSM fsm) {
    Collection<FSMTransition> uncovered = new ArrayList<FSMTransition>();
    uncovered.addAll(fsm.getTransitions());
    Map<FSMTransition, Integer> coverage = suite.getTransitionCoverage();
    uncovered.removeAll(coverage.keySet());
    int allCount = fsm.getTransitions().size();
    int uncoveredCount = uncovered.size();
    int coveredCount = allCount - uncoveredCount;
    double ratio = ((double) coveredCount) / ((double) allCount);
    return ratio >= threshold;
  }
}
