package osmo.tester.optimizer.reducer;

import osmo.common.Randomizer;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.optimizer.multi.MultiOSMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Teemu Kanstren
 */
public class Reducer {
  private static final Logger log = new Logger(Reducer.class);
  /** Shared configuration for all generators, only the seed will be different. */
  private OSMOConfiguration config = new OSMOConfiguration();
  /** How many generators to run in parallel? */
  private final int parallelism;
  /** The thread pool for running the generator tasks. */
  private final ExecutorService pool;
  /** The seed for creating more random seeds for the generators. */
  private final long seed;

  public Reducer(long seed) {
    parallelism = Runtime.getRuntime().availableProcessors();
    pool = Executors.newFixedThreadPool(parallelism);
    this.seed = seed;
  }

  public Reducer(int parallelism, long seed) {
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
   */
  public ReducerState search(long time, TimeUnit timeUnit, int populationSize, int length) {
    check();
    config.setSequenceTraceRequested(false);
    config.setExploring(true);
    config.setStopGenerationOnError(false);
    Collection<Future> futures = new ArrayList<>();
    Randomizer rand = new Randomizer(seed);
    ReducerState state = new ReducerState(length);
    for (int i = 0 ; i < parallelism ; i++) {
      ReducerTask task = new ReducerTask(config, rand.nextLong(), populationSize, state);
      Future future = pool.submit(task);
      log.debug("task submitted to pool");
      futures.add(future);
    }
    try {
      timeUnit.sleep(time);
      state.finish();
    } catch (InterruptedException e) {
      log.debug("Got insomnia, failed to sleep!??", e);
    }
    for (Future future : futures) {
      try {
        future.get();
      } catch (Exception e) {
        throw new RuntimeException("Failed to run a Reducer", e);
      }
    }
    pool.shutdown();
    int minimum = state.getMinimum();
    if (minimum < length) {
      System.out.println("Got down to:" + minimum);
    } else {
      System.out.println("Failed to find errors!");
    }
    Analyzer analyzer = new Analyzer(state);
    analyzer.analyze();
    analyzer.writeReport();
    return state;
  }

  private void check() {
    if (config.getFactory() instanceof SingleInstanceModelFactory) {
      System.out.println(MultiOSMO.ERROR_MSG);
    }
  }
}
