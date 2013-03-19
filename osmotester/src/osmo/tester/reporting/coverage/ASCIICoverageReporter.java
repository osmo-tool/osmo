package osmo.tester.reporting.coverage;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.coverage.TestCoverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Returns coverage tables in ASCII format
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstren
 */
public class ASCIICoverageReporter extends CoverageMetric {
  public ASCIICoverageReporter(Collection<TestCase> tests, TestCoverage tc, FSM fsm) {
    super(tests, tc, fsm);
  }

  public ASCIICoverageReporter(TestSuite ts, FSM fsm) {
    this(ts.getFinishedTestCases(), ts.getCoverage(), fsm);
  }

  public String getTransitionCounts() {
    int maxNumb = 0;
    Map<String, Integer> coverage = countTransitions();
    List<TransitionCount> counts = new ArrayList<>();

    for (Map.Entry<String, Integer> a : coverage.entrySet()) {
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
    for (int i = 0 ; i < a ; i++) {
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
      if (t.getFrom().length() > max) {
        max = t.getFrom().length();
      }
    }

    String ret = "";
    for (TransitionPairCount t : tpc) {
      ret += t.getFrom();
      ret += getSpaces(max - t.getFrom().length() + 2);
      ret += t.getTo();
      ret += getSpaces(max - t.getTo().length() + 2);
      ret += t.getCount() + "\n";

    }
    return ret;

  }

  public String getRequirementCounts() {
    //TODO: Implement
    return "";
  }

  public String getTraceabilityMatrix() {
    Collection<FSMTransition> transitions = fsm.getTransitions();
    Collection<String> all = new ArrayList<>();
    for (FSMTransition transition : transitions) {
      all.add(transition.getStringName());
    }

    int max = 0;
    for (String t : all) {
      if (t.length() > max) {
        max = t.length();
      }
    }

    String corner = "Coverage\\TC";
    String ret = corner + getSpaces(max - corner.length() + 2);
    for (int i = 0 ; i < tests.size() ; i++) {
      ret += "|" + i;
    }
    ret += "|\n";
    for (String t : all) {
      ret += t + getSpaces(max - t.length() + 2);
      for (TestCase tc : tests) {
        Collection<String> temp = tc.getCoveredTransitions();
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
