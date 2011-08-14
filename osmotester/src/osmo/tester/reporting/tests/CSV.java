package osmo.tester.reporting.tests;

import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;

import java.util.Collection;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class CSV {
  private final StepInfo info;

  public CSV(TestSuite suite, FSM fsm) {
    info = new StepInfo(suite, fsm);
  }

  public List<String> report() {
    return info.report("osmo/tester/reporting/tests/templates/step-info.csv");
  }
}
