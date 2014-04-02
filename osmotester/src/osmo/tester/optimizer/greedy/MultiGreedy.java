package osmo.tester.optimizer.greedy;

import osmo.common.Randomizer;
import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;
import osmo.tester.model.Requirements;
import osmo.tester.optimizer.CSVCoverageReport;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Runs multiple {@link GreedyOptimizer} instances in parallel to produce test variations. 
 * Finally runs a single overall optimization on the resulting test set to produce the final test suite.
 * User needs to provide a model factory to create model objects as each optimizer needs a different copy
 * of the model objects to run in parallel.
 * Note that with parallel executions, different executions might generate different test instances in different order.
 * The final optimized set can then contain different instances of test cases that add the same coverage criteria
 * to the test suite.
 * Thus the score achieved should be deterministic but the exact set of tests may have variation.
 *
 * @author Teemu Kanstren
 */
public class MultiGreedy {
  private static final Logger log = new Logger(MultiGreedy.class);
  /** Configuration for the optimizer(s). */
  private final ScoreConfiguration optimizerConfig;
  /** The thread pool for running the optimizer tasks. */
  private final ExecutorService greedyPool;
  /** The test model. */
  private FSM fsm = null;
  /** Optimizer timeout. See {@link GreedyOptimizer} for more info. */
  private int timeout = -1;
  /** For configuring the test generator. */
  private final OSMOConfiguration osmoConfig;
  /** How many tests should each optimizer generate in an iteration? */
  private int populationSize = 1000;
  /** Set of possible pairs of steps observed during generation. */
  private Collection<String> possiblePairs = new LinkedHashSet<>();
  /** For calculating coverage scores. */
  private final ScoreCalculator calculator;
  /** For randomization. */
  private final Randomizer rand;
  /** Number of optimizers to create. Defaults to number of processors on system. */
  private int optimizerCount = Runtime.getRuntime().availableProcessors();
  /** List of created optimizers. */
  private final List<GreedyOptimizer> optimizers = new ArrayList<>();
  /** Do we delete the osmo-output directory when starting up? */
  private boolean deleteOldOutput = false;
  /** Should data be traced across steps? Takes a lot of memory. */
  private boolean dataTrace = false;
  /** Coverage of the final set. */
  private TestCoverage finalCoverage = null;
  private final String midPath;
  /** If > 0 defines the maximum number of tests to return. */
  private int max = 0;
  private int threshold = 1;
  private final Collection<IterationListener> listeners = new HashSet<>();

  /**
   * Uses number of processors on system as default for number of threads in the thread pool.
   * That is, as the value for the parallelism parameter in the other constructor.
   * 
   * @param osmoConfig  Configuration for the generator.
   * @param scoreConfig Configuration for coverage score calculations.
   * @param seed The initial seed used to seed the randomizer used to seed the optimizers.
   */
  public MultiGreedy(OSMOConfiguration osmoConfig, ScoreConfiguration scoreConfig, long seed) {
    this(osmoConfig, scoreConfig, seed, Runtime.getRuntime().availableProcessors());
  }

  /**
   * @param osmoConfig  Configuration for the generator.
   * @param scoreConfig Configuration for coverage score calculations.
   * @param seed The initial seed used to seed the randomizer used to seed the optimizers.
   * @param parallelism How many threads to create in the thread pool.
   */
  public MultiGreedy(OSMOConfiguration osmoConfig, ScoreConfiguration scoreConfig, long seed, int parallelism) {
    this.osmoConfig = osmoConfig;
    this.optimizerConfig = scoreConfig;
    calculator = new ScoreCalculator(scoreConfig);
    greedyPool = Executors.newFixedThreadPool(parallelism);
    rand = new Randomizer(seed);
    optimizerCount = parallelism;
    midPath = "multi-greedy-"+seed+"/";
  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public int getThreshold() {
    return threshold;
  }

  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }

  public void enableDataTrace() {
    dataTrace = true;
  }

  public boolean isDeleteOldOutput() {
    return deleteOldOutput;
  }

  public void setDeleteOldOutput(boolean deleteOldOutput) {
    this.deleteOldOutput = deleteOldOutput;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  /**
   * Initializes a set of {@link GreedyOptimizer} instances to search for an optimal set of tests (test suite).
   *
   * @return The optimized set of tests.
   */
  public List<TestCase> search() {
    if (deleteOldOutput) TestUtils.recursiveDelete("osmo-output");
    
    long start = System.currentTimeMillis();
    List<TestCase> tests = generate();

    log.info("sorting set from all optimizers");
    //this does the final round of optimization for the set received from all optimizers..
    tests = GreedyOptimizer.sortAndPrune(tests, calculator, max);

    writeFinalReport(tests, rand.getSeed());
    updateRequirements(tests);

    log.info("search done");
    long end = System.currentTimeMillis();
    long seconds = (end-start)/1000;
    System.out.println("duration of search: "+seconds+"s.");

    for (IterationListener listener : listeners) {
      listener.generationDone(tests);
    }

    return tests;
  }

  /**
   * Runs all the optimizers, collects the tests.
   * 
   * @return Combined set of all generated tests from all optimizers.
   */
  private List<TestCase> generate() {
    log.info("Starting search with " + optimizerCount + " optimizers");
    Collection<Future<Collection<TestCase>>> futures = new ArrayList<>();

    runOptimizers(futures);
    List<TestCase> allTests = collectAllTests(futures);
    
    allTests = trimToMax(allTests);

    log.info("optimizers done");
    greedyPool.shutdown();
    collectReportData();
    
    return allTests;
  }

  private List<TestCase> trimToMax(List<TestCase> allTests) {
    if (max <= 0) return allTests;
    List<TestCase> tests = new ArrayList<>();
    for (TestCase test : allTests) {
      tests.add(test);
      if (tests.size() >= max) return tests;
    }
    return tests;
  }

  /**
   * Starts all optimizers runnning in the thread pool.
   * 
   * @param futures    Collects here all the {@link Future} objects for the running {@link GreedyTask} instances.
   */
  private void runOptimizers(Collection<Future<Collection<TestCase>>> futures) {
    for (int i = 0 ; i < optimizerCount ; i++) {
      GreedyOptimizer optimizer = new GreedyOptimizer(osmoConfig, optimizerConfig);
      optimizer.setMidPath(midPath);
      optimizer.setSubStatus(true);
      if (dataTrace) optimizer.enableDataTrace();
      optimizer.setTimeout(timeout);
      optimizer.setThreshold(threshold);
      for (IterationListener listener : listeners) {
        optimizer.addIterationListener(listener);
      }
      GreedyTask task = new GreedyTask(optimizer, rand.nextLong(), populationSize);
      Future<Collection<TestCase>> future = greedyPool.submit(task);
      log.debug("task submitted to pool");
      futures.add(future);
      optimizers.add(optimizer);
    }
  }

  /**
   * Waits for the thread pool to finish running all the optimizers and collects tests once they are done.
   * 
   * @param futures  Access to the tasks in the pool to wait for their completion and access results.
   * @return The combined set, not yet optimized in itself.
   */
  private List<TestCase> collectAllTests(Collection<Future<Collection<TestCase>>> futures) {
    List<TestCase> allTests = new ArrayList<>();
    for (Future<Collection<TestCase>> future : futures) {
      try {
        allTests.addAll(future.get());
      } catch (Exception e) {
        throw new RuntimeException("Failed to run a (Multi) GreedyOptimizer", e);
      }
    }
    return allTests;
  }

  /**
   * Collects data used to write the overall report for the optimizers.
   * Note that this may contain stuff that will not eventually be given to user.
   */
  private void collectReportData() {
    for (GreedyOptimizer optimizer : optimizers) {
      //we just need on instance of test model structure for reporting, does not matter which one
      fsm = optimizer.getFsm();
      //and collect all possible step pairs observed for report as well
      possiblePairs.addAll(optimizer.getPossiblePairs());
    }
  }

  /**
   * Updates the list of covered requirements to match the actual covered set.
   * 
   * @param cases The final set of generated tests that will be given to user. Should be fully optimized now.
   */
  private void updateRequirements(List<TestCase> cases) {
    //finally, we need to update the coverage in the FSM to reflect the final pruned suite
    //the coverage in fsm is used by coverage reporters which is why we need this
    Requirements reqs = fsm.getRequirements();
    TestCoverage coverage = new TestCoverage(cases);
    reqs.fillCoverage(coverage);
  }

  /**
   * Write the final report for the overall optimized test set.
   * Goes into "osmo-output/greedy-<seed>/final-scores.csv" file.
   * 
   * @param cases The final set of generated tests.
   * @param seed The seed used in configuring all the optimizers. Shown in report.
   */
  private void writeFinalReport(List<TestCase> cases, long seed) {
    String summary = "summary\n";
    summary += "tests: "+cases.size()+"\n";
    
    CSVCoverageReport report = new CSVCoverageReport(calculator);
    report.process(cases);

    finalCoverage = new TestCoverage(cases);
    summary += finalCoverage.coverageString(fsm, possiblePairs, null, null, null, false);

    String totalCsv = report.report();
    totalCsv += summary + "\n";
    String filename = createFinalReportPath();
    TestUtils.write(totalCsv, filename);
  }
  
  public String createFinalReportPath() {
    return "osmo-output/" + midPath + "final-scores.csv";
  }

  public FSM getFsm() {
    return fsm;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public int getTimeout() {
    return timeout;
  }

  public TestCoverage getFinalCoverage() {
    return finalCoverage;
  }

  public void addIterationListener(IterationListener listener) {
    this.listeners.add(listener);
  }
}
