package osmo.tester.reporting.coverage;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/**
 * This returns coverage tables in HTML format
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public class HTMLCoverageReporter extends CoverageMetric {
  public HTMLCoverageReporter(TestSuite ts, FSM fsm) {
    super(ts, fsm);
  }

  public String getTransitionCounts() {
    //note: for this to work, you need to have the IDE or build script copy the .html files to the same location on the output dir (alongside the java classes)
    return super.getTransitionCounts("osmo/tester/reporting/coverage/templates/transition-coverage.txt");
  }

  public String getTransitionPairCounts() {
    return super.getTransitionPairCounts("osmo/tester/reporting/coverage/templates/transitionpair-coverage.txt");
  }

  public String getRequirementCounts() {
    return super.getRequirementsCounts("osmo/tester/reporting/coverage/templates/requirement-coverage.txt");
  }

  public String getTraceabilityMatrix() {
    return super.getTraceabilityMatrix("osmo/tester/reporting/coverage/templates/coverage-matrix.txt");
  }

}
