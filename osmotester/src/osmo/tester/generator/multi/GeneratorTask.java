package osmo.tester.generator.multi;

import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.generator.endcondition.Time;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.optimizer.CSVReport;
import osmo.tester.reporting.trace.TraceReportWriter;

import java.util.List;
import java.util.concurrent.Callable;

/** @author Teemu Kanstren */
public class GeneratorTask implements Runnable {
  private final OSMOConfiguration config;
  private long seed;
  private final Time time;
  private final int id;
  private static volatile int nextId = 1;

  public GeneratorTask(OSMOConfiguration config, Time time, long seed) {
    this.config = config;
    this.seed = seed;
    this.time = time;
    this.id = nextId++;
  }

  @Override
  public void run() {
    time.init(0, null);
    int i = 1;
    while (!time.endTest(null, null)) {
      seed += 100;
      OSMOTester tester = new OSMOTester();
      tester.setConfig(config);
      tester.generate(seed);
      TestSuite suite = tester.getSuite();
      OSMOTester.writeTrace("osmo-output/mosmo-task-"+id+"-i-"+i, suite.getAllTestCases(), seed, config);
      i++;
    }
  }
}
