package osmo.tester.examples.report;

import osmo.common.TestUtils;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calculator.CalculatorModel;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.reporting.coverage.HTMLCoverageReporter;

/** @author Teemu Kanstren */
public class CalculatorMatrix {
  public static void main(String[] args) throws Exception {
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new CalculatorModel());
    osmo.addSuiteEndCondition(new Length(10));
    osmo.addTestEndCondition(new Length(5));
    osmo.generate();
    HTMLCoverageReporter reporter = new HTMLCoverageReporter(osmo.getSuite(), osmo.getFsm());
    String matrix = reporter.getTraceabilityMatrix();
    String file = "calculator-matrix.html";
    reporter.write(matrix, file);
  }
}
