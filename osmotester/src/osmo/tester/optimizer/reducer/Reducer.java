package osmo.tester.optimizer.reducer;

import osmo.common.Randomizer;
import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.optimizer.multiosmo.MultiOSMO;
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
  private final ReducerConfig config;
  private Randomizer rand;

  public Reducer(ReducerConfig config) {
    parallelism = config.getParallelism();
    pool = Executors.newFixedThreadPool(parallelism);
    this.config = config;
  }

  public OSMOConfiguration getOsmoConfig() {
    return osmoConfig;
  }

  /**
   * Starts search using the given generation configuration and given number of parallel threads.
   */
  public ReducerState search() {
    check();
    osmoConfig.setFailWhenNoWayForward(false);
    osmoConfig.setSequenceTraceRequested(false);
    osmoConfig.setExploring(true);
    osmoConfig.setStopGenerationOnError(false);
    rand = new Randomizer(config.getSeed());

    MainParser parser = new MainParser();
    ParserResult parserResult = parser.parse(0, osmoConfig.getFactory(), null);
    FSM fsm = parserResult.getFsm();
    Collection<FSMTransition> transitions = fsm.getTransitions();
    List<String> allSteps = new ArrayList<>();
    for (FSMTransition transition : transitions) {
      allSteps.add(transition.getStringName());
    }
    ReducerState state = new ReducerState(allSteps, config);

    state.setStopOnFirst(true);
    long totalTime = config.getTotalUnit().toMillis(config.getTotalTime());
    log.info("Running initial search");
    fuzz(state, totalTime);
    state.setStopOnFirst(false);
    state.resetDone();
    log.info("Running first shortening");
    shorten(state);
    //perform fuzzy search again but do not stop on first
    state.resetDone();
    long iterationTime = config.getIterationUnit().toMillis(config.getIterationTime());
    log.info("Running second search");
    fuzz(state, iterationTime);
    //perform another shortening attempt just in case fuzzy made a difference
    state.resetDone();
    log.info("Running second shortening");
    shorten(state);
    //perform final fuzzy for invariant capture
    state.resetDone();
    log.info("Running final search");
    fuzz(state, iterationTime);
    
    pool.shutdown();
    int minimum = state.getMinimum();
    if (minimum < config.getLength()) {
      System.out.println("Got down to:" + minimum);
    } else {
      System.out.println("Failed to find errors!");
    }
    writeReports(allSteps, state);
    return state;
  }
  
  private void writeReports(List<String> allSteps, ReducerState state) {
    Analyzer analyzer = new Analyzer(allSteps, state);
    analyzer.analyze();
    analyzer.writeReport("reducer-final");

    List<TestCase> tests = state.getTests();
    List<TestCase> traced = new ArrayList<>();
    int i = 1;
    for (TestCase test : tests) {
      i++;
      //only print max of 100 traces
      if (i > 100) break;
      traced.add(test);
    }
    String filename = analyzer.getPath()+"final-tests";
    OSMOTester.writeTrace(filename, traced, config.getSeed(), osmoConfig);
  }

  private void fuzz(ReducerState state, long waitTime) {
    Collection<Future> futures = new ArrayList<>();
    for (int i = 0 ; i < parallelism ; i++) {
      FuzzerTask task = new FuzzerTask(osmoConfig, rand.nextLong(), config, state);
      Future future = pool.submit(task);
      log.debug("task submitted to pool");
      futures.add(future);
    }
    try {
      //wait for search to finish
      synchronized (state) {
        //we might have finished before coming here if previous searches were 100% success
        if (!state.isDone()) state.wait(waitTime);
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
  }

  private void shorten(ReducerState state) {
    //perform possible shortening
    ShortenerTask task = new ShortenerTask(osmoConfig, rand.nextLong(), config, state);
    Future shortFuture = pool.submit(task);
    try {
      shortFuture.get();
    } catch (Exception e) {
      throw new RuntimeException("Failed to run a Shortener", e);
    }
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
