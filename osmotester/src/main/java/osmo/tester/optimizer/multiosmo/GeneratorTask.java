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
public class GeneratorTask implements Callable<GeneratorTask.Result> {
  /** The generator configuration. */
  private final OSMOConfiguration config;
  /** Base seed randomizer for the generators running in this task. */
  private final Randomizer seeder;
  /** The minimum time the iterations need to run. */
  private final Time time;
  /** A unique ID for this task. */
  private final int id;
  /** Next ID for next parallel task.. */
  private static volatile int nextId = 1;

  public GeneratorTask(OSMOConfiguration config, Time time, long seed) {
    this.config = config;
    this.seeder = new Randomizer(seed);
    this.time = time;
    this.id = nextId++;
  }

  @Override
  public GeneratorTask.Result call() throws Exception {
    GeneratorTask.Result result = new GeneratorTask.Result();
    time.init(0, null, config);
    while (!time.endTest(null, null)) {
      OSMOTester tester = new OSMOTester();
      tester.setConfig(config);
      long seed = seeder.nextLong();
      tester.generate(seed);
      List<TestCase> tests = tester.getSuite().getAllTestCases();
      result.addTests(tests);
    }
    return result;
  }

  public static class Result {
    private final List<TestCase> tests = new ArrayList<>();
    private final TestCoverage tc = new TestCoverage();

    public void addTests(List<TestCase> tests) {
      this.tests.addAll(tests);
      for (TestCase test : tests) {
        tc.addCoverage(test.getCoverage());
      }
    }

    public List<TestCase> getTests() {
      return tests;
    }

    public TestCoverage getCoverage() {
      return tc;
    }
  }
}
