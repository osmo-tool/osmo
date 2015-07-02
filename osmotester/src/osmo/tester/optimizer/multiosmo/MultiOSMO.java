package osmo.tester.optimizer.multiosmo;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.Time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Runs several generators in parallel, not optimizing the set but just splitting generation/execution over multiple
 * processors/cores at the same time. Mainly useful for online testing.
 * 
 * A convenience class, mainly same results could be achieved by just starting several separate generators at the same time
 * with different seeds.
 * 
 * Also intended to run the generator in iterations if intended to run very long online test sessions.
 * Works as follows:
 * -start the given number of threads to generate tests in parallel.
 * -runs each generator until it stops (suite end condition is met)
 * -if timeout for iterations is not met, re-starts the generator with another seed, iterates until time done
 * 
 * Configure by setting properties in the configuration object, instantiate generation via generate().
 * 
 * @author Teemu Kanstren 
 */
public class MultiOSMO {
  private static final Logger log = new Logger(MultiOSMO.class);
  /** Shared configuration for all generators, only the seed will be different. */
  private OSMOConfiguration config = new OSMOConfiguration();
  /** How many generators to run in parallel? */
  private final int parallelism;
  /** The thread pool for running the generator tasks. */
  private final ExecutorService pool;
  /** The seed for creating more random seeds for the generators. */
  private final long seed;
  public static final String ERROR_MSG = "WARNING: Using factory of type " + SingleInstanceModelFactory.class + ", which means all parallel tasks share the object instances.";

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

  /**
   * Starts generation using the given generation configuration and given number of parallel threads.
   * 
   * @param time The minimum time to run iterations.
   * @param reportAll Write generator traces to file for all generators?
   * @param printCoverage Do we want each generator to print achieved coverage in the end?
   * @return The coverage for generated tests.
   */
  public TestCoverage generate(Time time, boolean reportAll, boolean printCoverage) {
    check();
    config.setSequenceTraceRequested(false);
    config.setExploring(true);
    config.setStopGenerationOnError(false);
    Collection<Future<TestCoverage>> futures = new ArrayList<>();
    Randomizer rand = new Randomizer(seed);
    for (int i = 0 ; i < parallelism ; i++) {
      GeneratorTask task = new GeneratorTask(config, time, rand.nextLong(), reportAll, printCoverage);
      Future<TestCoverage> future = pool.submit(task);
      log.d("task submitted to pool");
      futures.add(future);
    }
    TestCoverage tc = new TestCoverage();
    for (Future<TestCoverage> future : futures) {
      try {
        TestCoverage ftc = future.get();
        tc.addCoverage(ftc);
      } catch (Exception e) {
        throw new RuntimeException("Failed to run a (Multi) OSMOTester", e);
      }
    }
    pool.shutdown();
    //TODO: write summary to file
    return tc;
  }
  
  private void check() {
    if (config.getFactory() instanceof SingleInstanceModelFactory) {
      System.out.println(ERROR_MSG);
    }
  }
}
