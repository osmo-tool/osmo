package osmo.tester.generator.multi;

import osmo.common.Randomizer;
import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.endcondition.Time;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.CSVReport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Runs several generators in parallel, not optimizing the set but just splitting generation/execution over multiple
 * processors/cores at the same time. Useful for online testing.
 * A convenience class, same results could be achieved by just starting several separate generators at the same time
 * with different seeds.
 * 
 * @author Teemu Kanstren 
 */
public class MultiOSMO {
  private static Logger log = new Logger(MultiOSMO.class);
  private OSMOConfiguration config = new OSMOConfiguration();
  private final int parallelism;
  /** The thread pool for running the generator tasks. */
  private final ExecutorService pool;
  private final long seed;

  public MultiOSMO(long seed) {
    parallelism = Runtime.getRuntime().availableProcessors();
    pool = Executors.newFixedThreadPool(parallelism);
    this.seed = seed;
  }

  public MultiOSMO(int parallelism, long seed) {
    this.parallelism = parallelism;
    pool = Executors.newFixedThreadPool(parallelism);
    this.seed = seed;
  }

  public OSMOConfiguration getConfig() {
    return config;
  }

  public void generate(Time time) {
    config.setTraceRequested(false);
    Collection<Future> futures = new ArrayList<>();
    Randomizer rand = new Randomizer(seed);
    for (int i = 0 ; i < parallelism ; i++) {
      GeneratorTask task = new GeneratorTask(config, time, rand.nextLong());
      Future future = pool.submit(task);
      log.debug("task submitted to pool");
      futures.add(future);
    }
    for (Future future : futures) {
      try {
        future.get();
      } catch (Exception e) {
        throw new RuntimeException("Failed to run a (Multi) OSMOTester", e);
      }
    }
    pool.shutdown();

//    String summary = "summary\n";
//    CSVReport report = new CSVReport(new ScoreCalculator(new ScoreConfiguration()));
//    report.process(allTests);
//
//    String totalCsv = report.report();
//    totalCsv += summary + "\n";
//    TestUtils.write(totalCsv, "osmo-output/mosmo-summary.csv");
  }
}
