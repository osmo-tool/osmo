package osmo.tester.optimizer;

import osmo.common.Randomizer;
import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.Requirements;
import osmo.tester.parser.MainParser;
import osmo.tester.parser.ParserResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Runs multiple GreedyOptimizers in parallel to produce test variations, and in the end runs a single overall
 * optimization on the resulting set to produce the final test suite.
 * User needs to provide a factory to create configurations for the optimizer, as each one needs a different copy
 * with different model object instances, which are stored in the configuration, and a different seed for each
 * optimizer.
 * User also needs to provide a model factory for creating the model objects to be stored in the new configuration.
 * Note that with parallel executions, different executions might generate different test instances in different order.
 * The final optimized set can then contain different instances of test cases that add the same coverage criteria
 * to the test suite.
 * Thus the score achieved should be deterministic but the exact set of tests may have variation.
 *
 * @author Teemu Kanstren
 */
public class MultiGreedy {
  private static Logger log = new Logger(MultiGreedy.class);
  /** Configuration for the optimizer(s). */
  private final ScoreConfiguration optimizerConfig;
  /** The thread pool for running the optimizer tasks. */
  private final ExecutorService greedyPool;
  /** The test model. */
  private FSM fsm = null;
  /** Optimizer timeout. See {@link GreedyOptimizer} for more info. */
  private int timeout = 1;
  /** For configuring the test generator. */
  private final OSMOConfiguration osmoConfig;
  private int populationSize = 1000;
  private Collection<String> possiblePairs = new HashSet<>();
  private final ScoreCalculator calculator;

  public MultiGreedy(OSMOConfiguration osmoConfig, ScoreConfiguration optimizerConfig) {
    this(osmoConfig, optimizerConfig, Runtime.getRuntime().availableProcessors());
  }

  public MultiGreedy(OSMOConfiguration osmoConfig, ScoreConfiguration optimizerConfig, int parallelism) {
    this.osmoConfig = osmoConfig;
    this.optimizerConfig = optimizerConfig;
    calculator = new ScoreCalculator(optimizerConfig);
    greedyPool = Executors.newFixedThreadPool(parallelism);
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
   * @param optimizerCount The number of optimizers to instantiate.
   * @return The optimized set of tests.
   */
  public List<TestCase> search(int optimizerCount, long seed) {
    long start = System.currentTimeMillis();
    List<TestCase> tests = generate(optimizerCount, seed);

    log.info("sorting set from all optimizers");
    tests = GreedyOptimizer.sortAndPrune(tests, calculator);

    writeFinalReport(tests, seed);
    updateRequirements(tests, seed);

    log.info("search done");
    long end = System.currentTimeMillis();
    long seconds = (end-start)/1000;
    System.out.println("duration of search: "+seconds+"s.");
    return tests;
  }
  
  private List<TestCase> generate(int optimizerCount, long seed) {
    log.info("Starting search with " + optimizerCount + " optimizers");
    Collection<Future<List<TestCase>>> futures = new ArrayList<>();
    List<GreedyOptimizer> optimizers = new ArrayList<>();
    Randomizer rand = new Randomizer(seed);
    for (int i = 0 ; i < optimizerCount ; i++) {
      GreedyOptimizer optimizer = new GreedyOptimizer(osmoConfig, optimizerConfig);
      optimizer.setTimeout(timeout);
      GreedyTask task = new GreedyTask(optimizer, rand.nextLong(), populationSize);
      Future<List<TestCase>> future = greedyPool.submit(task);
      log.debug("task submitted to pool");
      futures.add(future);
      optimizers.add(optimizer);
    }
    List<TestCase> allTests = new ArrayList<>();
    for (Future<List<TestCase>> future : futures) {
      try {
        allTests.addAll(future.get());
      } catch (Exception e) {
        throw new RuntimeException("Failed to run a (Multi) GreedyOptimizer", e);
      }
    }
    log.info("optimizers done");
    greedyPool.shutdown();
    for (GreedyOptimizer optimizer : optimizers) {
      fsm = optimizer.getFsm();
      possiblePairs.addAll(optimizer.getPossiblePairs());
    }
    return allTests;
  }

  private void updateRequirements(List<TestCase> cases, long seed) {
    //finally, we need to update the coverage in the FSM to reflect the final pruned suite
    //the coverage in fsm is used by coverage reporters which is why we need this
    Requirements reqs = fsm.getRequirements();
    reqs.clearCoverage();
    TestCoverage coverage = new TestCoverage(cases);
    Collection<String> coveredReqs = coverage.getRequirements();
    for (String req : coveredReqs) {
      reqs.covered(req);
    }
  }

  private void writeFinalReport(List<TestCase> cases, long seed) {
    String summary = "summary\n";
    
    CSVReport report = new CSVReport(calculator);
    report.process(cases);

    TestCoverage tc = new TestCoverage(cases);
    summary += tc.coverageString(fsm, possiblePairs, null, null, null, false);

    String totalCsv = report.report();
    totalCsv += summary + "\n";
    TestUtils.write(totalCsv, "osmo-output/greedy-" + seed + "/final-scores.csv");
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
}
