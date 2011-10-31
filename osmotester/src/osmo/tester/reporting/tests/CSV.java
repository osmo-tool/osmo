package osmo.tester.reporting.tests;

import osmo.tester.generator.testsuite.TestSuite;

import java.util.List;

/** @author Teemu Kanstren */
public class CSV {
  private final ParameterReportBuilder info;

  public CSV(TestSuite suite) {
    info = new ParameterReportBuilder(suite);
  }

  public void addParameter(String name, Object value) {
    info.addParameter(name, value);
  }

  public List<String> report() {
    return info.report("osmo/tester/reporting/tests/templates/step-info.csv");
  }
}
