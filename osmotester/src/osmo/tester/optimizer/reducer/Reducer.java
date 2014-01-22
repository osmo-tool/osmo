package osmo.tester.optimizer.reducer;

import osmo.common.Randomizer;
import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.multi.MultiOSMO;
import osmo.tester.parser.MainParser;
import osmo.tester.parser.ParserResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Teemu Kanstren
 */
public class Reducer {
  private static final Logger log = new Logger(Reducer.class);
  /** Shared configuration for all generators, only the seed will be different. */
  private OSMOConfiguration osmoConfig = new OSMOConfiguration();
  /** How many generators to run in parallel? */
  private final int parallelism;
  /** The thread pool for running the generator tasks. */
  private final ExecutorService pool;
  private boolean deleteOldOutput;

  public Reducer() {
    parallelism = Runtime.getRuntime().availableProcessors();
    pool = Executors.newFixedThreadPool(parallelism);
  }

  public Reducer(int parallelism) {
    this.parallelism = parallelism;
    pool = Executors.newFixedThreadPool(parallelism);
  }

  public OSMOConfiguration getOsmoConfig() {
    return osmoConfig;
  }

  /**
   * Starts generation using the given generation configuration and given number of parallel threads.
   *
   * @param config Reducer configuration.
   */
  public ReducerState search(ReducerConfig config) {
    check();
    osmoConfig.setSequenceTraceRequested(false);
    osmoConfig.setExploring(true);
    osmoConfig.setStopGenerationOnError(false);
    Collection<Future> futures = new ArrayList<>();
    Randomizer rand = new Randomizer(config.getSeed());

    MainParser parser = new MainParser();
    ParserResult parserResult = parser.parse(0, osmoConfig.getFactory(), null);
    FSM fsm = parserResult.getFsm();
    Collection<FSMTransition> transitions = fsm.getTransitions();
    List<String> steps = new ArrayList<>();
    for (FSMTransition transition : transitions) {
      steps.add(transition.getStringName());
    }
    ReducerState state = new ReducerState(steps, config);

    for (int i = 0 ; i < parallelism ; i++) {
      ReducerTask task = new ReducerTask(osmoConfig, rand.nextLong(), config, state);
      Future future = pool.submit(task);
      log.debug("task submitted to pool");
      futures.add(future);
    }
    try {
      //wait for reducer to finish
      synchronized (state) {
        config.getTotalUnit().timedWait(state, config.getTotalTime());
      }
      //if overall timeout instead of reduction, we terminate searches
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
    if (minimum < config.getLength()) {
      System.out.println("Got down to:" + minimum);
    } else {
      System.out.println("Failed to find errors!");
    }
    Analyzer analyzer = new Analyzer(steps, state);
    analyzer.analyze();
    analyzer.writeReport("reducer-final");
    return state;
  }

  private void check() {
    if (osmoConfig.getFactory() instanceof SingleInstanceModelFactory) {
      System.out.println(MultiOSMO.ERROR_MSG);
    }
    if (deleteOldOutput) {
      TestUtils.recursiveDelete("osmo-output");
    }
  }

  public void setDeleteOldOutput(boolean deleteOldOutput) {
    this.deleteOldOutput = deleteOldOutput;
  }

  public boolean isDeleteOldOutput() {
    return deleteOldOutput;
  }
}
