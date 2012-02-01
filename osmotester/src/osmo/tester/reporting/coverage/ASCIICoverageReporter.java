package osmo.tester.reporting.coverage;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Returns coverage tables in ASCII format
 *
 * @author Olli-Pekka Puolitaival
 */
public class ASCIICoverageReporter extends CoverageMetric {
  public ASCIICoverageReporter(TestSuite ts, FSM fsm) {
    super(ts, fsm);
  }

  public String getTransitionCounts() {
    int maxNumb = 0;
    Map<FSMTransition, Integer> coverage = countTransitions();
    List<TransitionCount> counts = new ArrayList<TransitionCount>();

    for (Map.Entry<FSMTransition, Integer> a : coverage.entrySet()) {
      TransitionCount count = new TransitionCount(a.getKey(), a.getValue());
      counts.add(count);
      if (count.getName().length() > (maxNumb))
        maxNumb = count.getName().length();
    }
    Collections.sort(counts);

    String ret = "";
    for (TransitionCount t : counts) {
      ret += t.getName();
      ret += getSpaces(maxNumb - t.getName().length() + 2);
      ret += t.getCount() + "\n";
    }
    return ret;
  }

  private String getSpaces(int a) {
    String ret = "";
    for (int i = 0; i < a; i++) {
      ret += " ";
    }
    return ret;
  }

  /**
   * Output something like this
   * transition1   transition2  2
   * transition2   transition3  0
   */
  public String getTransitionPairCounts() {
    List<TransitionPairCount> tpc = countTransitionPairs();
    Collections.sort(tpc);

    //Find longest
    int max = 0;
    for (TransitionPairCount t : tpc) {
      if (t.getFrom().getName().length() > max) {
        max = t.getFrom().getName().length();
      }
    }

    String ret = "";
    for (TransitionPairCount t : tpc) {
      ret += t.getFrom().getName();
      ret += getSpaces(max - t.getFrom().getName().length() + 2);
      ret += t.getTo().getName();
      ret += getSpaces(max - t.getTo().getName().length() + 2);
      ret += t.getCount() + "\n";

    }
    return ret;

  }

  public String getRequirementCounts() {
    //TODO: Implement
    return "";
  }

  public String getTraceabilityMatrix() {
    List<TestCase> testcases = testSuite.getFinishedTestCases();
    Collection<FSMTransition> all = fsm.getTransitions();

    int max = 0;
    for (FSMTransition t : all) {
      if (t.getName().length() > max) {
        max = t.getName().length();
      }
    }

    String corner = "Coverage\\TC";
    String ret = corner + getSpaces(max - corner.length() + 2);
    for (int i = 0; i < testcases.size(); i++) {
      ret += "|" + i;
    }
    ret += "|\n";
    for (FSMTransition t : all) {
      ret += t.getName() + getSpaces(max - t.getName().length() + 2);
      for (TestCase tc : testcases) {
        Collection<FSMTransition> temp = tc.getCoveredTransitions();
        if (temp.contains(t))
          ret += "|x";
        else
          ret += "| ";
      }
      ret += "|\n";
    }

    return ret;
  }
}
