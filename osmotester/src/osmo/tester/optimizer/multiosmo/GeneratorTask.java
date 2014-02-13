package osmo.tester.optimizer.multiosmo;

import osmo.common.Randomizer;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.endcondition.Time;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/** 
 * Runs a single generator in a thread of its own as executed in a thread pool by {@link MultiOSMO}.
 * 
 * @author Teemu Kanstren 
 */
public class GeneratorTask implements Callable<TestCoverage> {
  /** The generator configuration. */
  private final OSMOConfiguration config;
  /** Base seed randomizer for the generators running in this task. */
  private final Randomizer seeder;
  /** The minimum time the iterations need to run. */
  private final Time time;
  /** A unique ID for this task, for writing traces. */
  private final int id;
  /** Next ID for next parallel task.. */
  private static volatile int nextId = 1;
  private final boolean traceAll;
  private final boolean printCoverage;

  public GeneratorTask(OSMOConfiguration config, Time time, long seed, boolean traceAll, boolean printCoverage) {
    this.config = config;
    this.seeder = new Randomizer(seed);
    this.time = time;
    this.id = nextId++;
    this.traceAll = traceAll;
    this.printCoverage = printCoverage;
  }

  @Override
  public TestCoverage call() throws Exception {
    TestCoverage tc = new TestCoverage();
    time.init(0, null, config);
    int i = 1;
    while (!time.endTest(null, null)) {
      OSMOTester tester = new OSMOTester();
      tester.setConfig(config);
      tester.setPrintCoverage(printCoverage);
      long seed = seeder.nextLong();
      tester.generate(seed);
      TestSuite suite = tester.getSuite();
      List<TestCase> tests = suite.getAllTestCases();
      TestCoverage tc2 = new TestCoverage(tests);
      tc.addCoverage(tc2);
      if (traceAll) {
        OSMOTester.writeTrace("osmo-output/mosmo-task-"+id+"-i-"+i, tests, seed, config);
      } else {
        List<TestCase> failed = new ArrayList<>();
        for (TestCase test : tests) {
          if (test.isFailed()) {
            failed.add(test);
          }
        }
        if (failed.size() > 0) OSMOTester.writeTrace("osmo-output/mosmo-task-"+id+"-i-"+i, failed, seed, config);
      }
      i++;
    }
    return tc;
  }
}
