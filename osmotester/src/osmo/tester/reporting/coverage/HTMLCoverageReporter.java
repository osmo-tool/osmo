package osmo.tester.reporting.coverage;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.coverage.TestCoverage;

import java.util.Collection;

/**
 * This returns coverage tables in HTML format
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public class HTMLCoverageReporter extends CoverageMetric {
  public HTMLCoverageReporter(Collection<TestCase> tests, TestCoverage tc, FSM fsm) {
    super(tests, tc, fsm);
  }

  public HTMLCoverageReporter(TestSuite ts, FSM fsm) {
    this(ts.getFinishedTestCases(), ts.getCoverage(), fsm);
  }

  public String getTransitionCounts() {
    //note: for this to work, you need to have the IDE or build script copy the .html files to the same location on the output dir (alongside the java classes)
    return super.getTransitionCounts("osmo/tester/reporting/coverage/templates/transition-coverage.txt");
  }

  public String getTransitionPairCounts() {
    return super.getTransitionPairCounts("osmo/tester/reporting/coverage/templates/transitionpair-coverage.txt");
  }

  public String getTagCounts() {
    return super.getRequirementCounts("osmo/tester/reporting/coverage/templates/req-coverage.txt");
  }

  public String getTraceabilityMatrix() {
    return super.getTraceabilityMatrix("osmo/tester/reporting/coverage/templates/coverage-matrix.txt");
  }

}
