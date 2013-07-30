package osmo.tester.optimizer;

import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;

import java.util.Collection;
import java.util.List;

/** @author Teemu Kanstren */
public class CSVReport {
  private final ScoreCalculator scoreCalculator;
  private String coverage = "cumulative coverage per test\n";
  private String gain = "gained coverage per test\n";
  private String testCount = "number of tests in suite\n";
  private String totalScore = "total score\n";
  private String steps = "number of steps\n";
  private String stepPairs = "number of step pairs\n";
  private String states = "number of states\n";
  private String statePairs = "number of state pairs\n";
  private String reqs = "number of requirements\n";
  private String length = "length\n";

  public CSVReport(ScoreCalculator scoreCalculator) {
    this.scoreCalculator = scoreCalculator;
  }

  public String report() {
    String report = "";
    report += coverage;
    report += gain;
    report += testCount;
    report += totalScore;
    report += steps;
    report += stepPairs;
    report += states;
    report += statePairs;
    report += reqs;
    report += length;
    return report;
  }

  /**
   * CSV row of coverage for each test, to use in excel etc.
   *
   * @param tests Create coverage CSV for these.
   */
  public void process(Collection<TestCase> tests) {
    TestCoverage tc = new TestCoverage();
    for (TestCase test : tests) {
      int old = scoreCalculator.calculateScore(tc);
      tc.addTestCoverage(test);
      int now = scoreCalculator.calculateScore(tc);
      int gain = now - old;
      this.gain += gain + "; ";

      coverage += scoreCalculator.calculateScore(tc) + "; ";
      reqs += tc.getRequirements().size() + "; ";
      length += tc.getSteps().size() + "; ";
      steps += tc.getSingles().size() + "; ";
      stepPairs += tc.getStepPairs().size() + "; ";
      states += tc.getStates().size() + "; ";
      statePairs += tc.getStatePairs().size() + "; ";
    }
    this.gain += "\n";
    coverage += "\n";
    reqs += "\n";
    length += "\n";
    steps += "\n";
    stepPairs += "\n";
    states += "\n";
    statePairs += "\n";

    testCount += tests.size();
    testCount += "\n";

    totalScore += scoreCalculator.calculateScore(tc);
    totalScore += "\n";
  }
}
