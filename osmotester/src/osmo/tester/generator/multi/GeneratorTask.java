package osmo.tester.generator.multi;

import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.optimizer.CSVReport;
import osmo.tester.reporting.trace.TraceReportWriter;

import java.util.List;
import java.util.concurrent.Callable;

/** @author Teemu Kanstren */
public class GeneratorTask implements Callable<List<TestCase>> {
  private final OSMOConfiguration config;
  private final long seed;

  public GeneratorTask(OSMOConfiguration config, long seed) {
    this.config = config;
    this.seed = seed;
  }

  @Override
  public List<TestCase> call() throws Exception {
    OSMOTester tester = new OSMOTester();
    tester.setConfig(config);
    tester.generate(seed);
    TestSuite suite = tester.getSuite();
    List<TestCase> tests = suite.getAllTestCases();

    String summary = "summary\n";
    CSVReport report = new CSVReport(new ScoreCalculator(new ScoreConfiguration()));
    report.process(tests);

    String totalCsv = report.report();
    totalCsv += summary + "\n";
    String filename = "osmo-output/osmo-" + seed;
    TestUtils.write(totalCsv, filename + ".csv");
    TraceReportWriter trace = new TraceReportWriter();
    trace.write(suite, filename+".html");

    return tests;
  }
}
