package osmo.tester.optimizer.multiosmo;

import osmo.common.Randomizer;
import osmo.common.Logger;
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
  public static final String ERROR_MSG = "WARNING: Using factory of type " + SingleInstanceModelFactory.class + ", which means all parallel tasks share the object instances.";

  public MultiOSMO() {
    parallelism = Runtime.getRuntime().availableProcessors();
    pool = Executors.newFixedThreadPool(parallelism);
  }

  public MultiOSMO(int parallelism) {
    this.parallelism = parallelism;
    pool = Executors.newFixedThreadPool(parallelism);
  }

  public OSMOConfiguration getConfig() {
    return config;
  }

  /**
   * Starts generation using the given generation configuration and given number of parallel threads.
   *
   * @param seed The seed for creating more random seeds for the generators.
   * @param time The minimum time to run iterations.
   * @return The coverage for generated tests.
   */
  public GeneratorTask.Result generate(Time time, long seed) {
    check();
    Collection<Future<GeneratorTask.Result>> futures = new ArrayList<>();
    Randomizer rand = new Randomizer(seed);
    for (int i = 0 ; i < parallelism ; i++) {
      GeneratorTask task = new GeneratorTask(config, time, rand.nextLong());
      Future<GeneratorTask.Result> future = pool.submit(task);
      log.d("task submitted to pool");
      futures.add(future);
    }
    GeneratorTask.Result result = new GeneratorTask.Result();
    for (Future<GeneratorTask.Result> future : futures) {
      try {
        GeneratorTask.Result ftc = future.get();
        result.addTests(ftc.getTests());
      } catch (Exception e) {
        throw new RuntimeException("Failed to run a (Multi) OSMOTester", e);
      }
    }
    pool.shutdown();
    return result;
  }
  
  private void check() {
    if (config.getFactory() instanceof SingleInstanceModelFactory) {
      System.out.println(ERROR_MSG);
    }
  }
}
