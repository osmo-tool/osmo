package osmo.tester.reporting.coverage;

import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Returns coverage tables in ASCII format
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstren
 */
public class ASCIICoverageReporter extends CoverageMetric {
  public ASCIICoverageReporter(TestCoverage tc, Collection<TestCase> tests, FSM fsm) {
    super(tc, tests, fsm);
  }

  public String getStepCounts() {
    List<ValueCount> coverage = countSteps();
    int longest = 0;
    for (ValueCount tc : coverage) {
      if (tc.getValue().length() > longest) {
        longest = tc.getValue().length();
      }
    }

    StringBuilder ret = new StringBuilder();
    for (ValueCount t : coverage) {
      ret.append(t.getValue());
      ret.append(getSpaces(longest - t.getValue().length() + 2));
      ret.append(t.getCount()).append("\n");
    }
    return ret.toString();
  }

  private String getSpaces(int a) {
    StringBuilder ret = new StringBuilder();
    for (int i = 0 ; i < a ; i++) {
      ret.append(" ");
    }
    return ret.toString();
  }

  /**
   * Output something like this
   * step1   step2  2
   * step2   step3  0
   * 
   * @return Formatted report for step-pairs.
   */
  public String getStepPairCounts() {
    List<ValueCount> tpc = countStepPairs();
    Collections.sort(tpc);

    //Find longest
    int max = 0;
    for (ValueCount t : tpc) {
      int length = t.getValue().length();
      if (length > max) {
        max = length;
      }
    }

    StringBuilder ret = new StringBuilder();
    for (ValueCount t : tpc) {
      ret.append(t.getValue());
      ret.append(getSpaces(max - t.getValue().length() + 2));
      ret.append(t.getCount()).append("\n");

    }
    return ret.toString();

  }

  public String getRequirementCounts() {
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
    StringBuilder ret = new StringBuilder(corner + getSpaces(max - corner.length() + 2));
    for (int i = 0 ; i < tests.size() ; i++) {
      ret.append("|").append(i);
    }
    ret.append("|\n");
    for (String t : all) {
      ret.append(t).append(getSpaces(max - t.length() + 2));
      for (TestCase tc : tests) {
        Collection<String> temp = tc.getCoveredSteps();
        if (temp.contains(t))
          ret.append("|x");
        else
          ret.append("| ");
      }
      ret.append("|\n");
    }

    return ret.toString();
  }
}
