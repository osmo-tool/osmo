package osmo.tester.examples.report;

import osmo.common.TestUtils;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calculator.CalculatorModel;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.reporting.coverage.HTMLCoverageReporter;

/** @author Teemu Kanstren */
public class CalculatorMatrix {
  public static void main(String[] args) throws Exception {
    OSMOTester osmo = new OSMOTester();
    SingleInstanceModelFactory factory = new SingleInstanceModelFactory();
    osmo.setModelFactory(factory);
    factory.add(new CalculatorModel());
    osmo.setSuiteEndCondition(new Length(10));
    osmo.setTestEndCondition(new Length(5));
    osmo.generate(2342);
    HTMLCoverageReporter reporter = new HTMLCoverageReporter(osmo.getSuite(), osmo.getFsm());
    String matrix = reporter.getTraceabilityMatrix();
    String file = "calculator-matrix.html";
    TestUtils.write(matrix, file);
  }
}
