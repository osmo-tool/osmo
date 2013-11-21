package osmo.tester.reporting.coverage;

import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.Collection;

/**
 * Returns coverage tables in comma separate value (CSV) format
 *
 * @author Olli-Pekka Puolitaival, Teemu Kanstrén
 */
public class CSVCoverageReporter extends CoverageMetric {
  public CSVCoverageReporter(TestCoverage tc, Collection<TestCase> tests, FSM fsm) {
    super(tc, tests, fsm);
  }

  public String getStepCounts() {
    //note: for this to work, you need to have the IDE or build script copy the .csv files to the same location on the output dir (alongside the java classes)
    return super.getStepCounts("osmo/tester/reporting/coverage/templates/step-coverage.csv");
  }

  public String getStepPairCounts() {
    return super.getStepPairCounts("osmo/tester/reporting/coverage/templates/steppair-coverage.csv");
  }

  public String getRequirementCounts() {
    return super.getRequirementCounts("osmo/tester/reporting/coverage/templates/req-coverage.csv");
  }

  public String getTraceabilityMatrix() {
    return super.getTraceabilityMatrix("osmo/tester/reporting/coverage/templates/coverage-matrix.csv");
  }
}
