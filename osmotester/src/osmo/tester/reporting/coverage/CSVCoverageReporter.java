package osmo.tester.reporting.coverage;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

/**
 * Returns coverage tables in comma separate value (CSV) format
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public class CSVCoverageReporter extends CoverageMetric {
  public CSVCoverageReporter(TestSuite ts, FSM fsm) {
    super(ts, fsm);
  }

  public String getTransitionCounts() {
    //note: for this to work, you need to have the IDE or build script copy the .csv files to the same location on the output dir (alongside the java classes)
    return super.getTransitionCounts("osmo/tester/reporting/coverage/templates/transition-coverage.csv");
  }

  public String getTransitionPairCounts() {
    return super.getTransitionPairCounts("osmo/tester/reporting/coverage/templates/transitionpair-coverage.csv");
  }

  public String getRequirementCounts() {
    return super.getRequirementsCounts("osmo/tester/reporting/coverage/templates/requirement-coverage.csv");
  }

  public String getTraceabilityMatrix() {
    return super.getTraceabilityMatrix("osmo/tester/reporting/coverage/templates/coverage-matrix.csv");
  }
}
