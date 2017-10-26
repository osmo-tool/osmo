package osmo.tester.optimizer.greedy;

import osmo.common.TestUtils;
import osmo.common.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;
import osmo.tester.model.Requirements;
import osmo.tester.optimizer.CSVCoverageReport;
import osmo.tester.optimizer.GenerationResults;
import osmo.tester.optimizer.TestSorter;
import osmo.tester.optimizer.multiosmo.MultiOSMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Generates test cases and greedily optimizes the resulting test suite with regards to coverage criteria as
 * given in an {@link osmo.tester.coverage.ScoreConfiguration}.
 * Resulting suite is optimised so the first test in the given set has the highest overall coverage score values,
 * the second one adds most score to that one as evaluated in terms of single test cases, the third adds the most
 * to these two, and so on.
 * New test cases are generated as long as the optimization of the suite in an iteration yields a higher value
 * than the previous iterations.
 * Best tests (ones that added anything to the suite score) are kept for the next round.
 * Each round (iteration) consists of generating a set of tests. The number of tests generated is equal to the
 * population size defined in the configuration.
 * The threshold to keep generating more tests is if any added score is observed.
 * This version generates test cases sequentially, using a single core on a single machine.
 * For a multi-core version, check the {@link MultiGreedy} version. However, note that the parallel versions are
 * not deterministic in the exact test cases returned, only in the scores they give (or should be).
 *
 * @author Teemu Kanstren
 */
public class GreedyOptimizer {
  private static final Logger log = new Logger(GreedyOptimizer.class);
  /** Defines weights for different coverage requirements to optimize for. */
  private final ScoreConfiguration config;
  /** The test model. */
  private FSM fsm = null;
  /** Identifier for next greedy optimizer if several are created. */
  private static int nextId = 1;
  /** The identifier for this optimizer. */
  public final int id = nextId++;
  /** Used to calculate coverage scores for different tests and suites. */
  private final ScoreCalculator scoreCalculator;
  /** How much does an iteration need to gain in score to go for another iteration? Defaults to 1. */
  private int threshold = 1;
  /** Maximum number of iterations to run. 0 means to run forever (until threshold or timeout). */
  private int maxIterations = 0;
  /** Seconds until the search times out. Timeout is checked between iterations and refers to how long overall generation progresses. */
  private long timeout = -1;
  /** For tracking all the path options encountered. */
  private Collection<String> possiblePairs = new LinkedHashSet<>();
  /** Generator configuration. */
  private final OSMOConfiguration osmoConfig;
  /** Start time of optimization. */
  private long start = 0;
  /** Final optimized suite. */
  private List<TestCase> suite = new ArrayList<>();
  /** Number of iteration running. */
  private int iteration = 0;
  /** Text to append to middle of filename when writing output reports. */
  private String midPath = "";
  /** Randomization seed to randomize generators with. */
  private long seed = 0;
  /** If > 0 defines the maximum number of tests to return. */
  private int max = 0;
  /** Listeners to notify about iterations. */
  private Collection<IterationListener> listeners = new HashSet<>();
  /** If we are running as sub-optimizer for multi-greedy we do not notify about final generation finished. */
  private boolean subStatus;

  /** @param osmoConfig Generator configuration to use in optimizers doing generation.
   * @param configuration For scoring the search. */
  public GreedyOptimizer(OSMOConfiguration osmoConfig, ScoreConfiguration configuration) {
    this.osmoConfig = osmoConfig;
    osmoConfig.setDataTraceRequested(false);
    this.config = configuration;
    this.scoreCalculator = new ScoreCalculator(configuration);
  }

  public void setMidPath(String midPath) {
    this.midPath = midPath;
  }

  /**
   * Sets the maximum length.
   * Note that this may produce a test suite with less score than just generating a full one and trimming that.
   * To avoid, use large population size or disable threshold with negative value.
   *
   * @param max maximum size.
   */
  public void setMax(int max) {
    this.max = max;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
  }

  public void enableDataTrace() {
    osmoConfig.setDataTraceRequested(true);
  }

  /**
   * You can set any threshold you like. Zero or less means going on forever. Timeout is recommended in such case.
   *
   * @param threshold Required added coverage score for iteration to continue with another iteration.
   */
  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }

  /**
   * @param timeout Generation timeout in seconds.
   */
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  /**
   * Use default of 1000 for population size.
   *
   * @param seed Generation seed.
   * @return The optimizer results.
   */
  public GenerationResults search(long seed) {
    return search(1000, seed);

  }

  /**
   * Performs a search following the defined search configuration.
   * Provides a sorted list of test cases, where the one with highest fitness is first, one that
   * adds most to this is second, one that adds most to those two is third and so on.
   * The set of tests is chosen by first generating a number of tests equals to populationSize.
   * From this overall set, the subset that gives some added coverage is returned.
   * If a test adds no coverage to the overall set, it is not returned.
   *
   * @param populationSize Number of tests to generate.
   * @param seed Randomization seed to randomize the generator.
   * @return The sorted set of test cases, with requested number of tests.
   */
  public GenerationResults search(int populationSize, long seed) {
    check();
    this.seed = seed;

    CSVCoverageReport report = new CSVCoverageReport(scoreCalculator);
    MainGenerator generator = configure(seed);
    generate(report, generator, populationSize);

    this.possiblePairs = generator.getPossibleStepPairs();
    TestCoverage suiteCoverage = new TestCoverage(suite);
    writeReport(report, suiteCoverage, suite.size(), iteration * populationSize);

    updateRequirementsCoverage(suiteCoverage);

    if (!subStatus) {
      for (IterationListener listener : listeners) {
        listener.generationDone(suite);
      }
    }
    return new GenerationResults(suite);
  }

  private void generate(CSVCoverageReport report, MainGenerator generator, int populationSize) {
    suite = new ArrayList<>();
    start = System.currentTimeMillis();
    int gain = Integer.MAX_VALUE;
    int previousScore = 0;
    long endTime = -1;
    if (timeout > 0) {
      //timeout is given in seconds so we multiple by 1000 to get milliseconds
      endTime = System.currentTimeMillis() + timeout * 1000;
    }
    log.i("greedy " + id + " starting up, population size " + populationSize);
    //to get a shorter test suite, use negative length weight.. in most cases should be no problem
    while (shouldRun(gain, iteration)) {
      long iStart = System.currentTimeMillis();
      log.i(id + ":starting iteration " + iteration);
      iteration++;

      for (int i = 0; i < populationSize; i++) {
        log.d("creating test case " + i);
        TestCase testCase = generator.nextTest();
        suite.add(testCase);
      }
      log.i(id + ":sorting and pruning iteration results");
      suite = sortAndPrune(id, suite, scoreCalculator, max);
      //we process each iteration to produce a list of how it was overall progressing
      report.process(suite);
      TestCoverage suiteCoverage = new TestCoverage(suite);

      int score = scoreCalculator.calculateScore(suiteCoverage);
      gain = score - previousScore;
      previousScore = score;

      for (IterationListener listener : listeners) {
        listener.iterationDone(suite);
      }

      long diff = System.currentTimeMillis() - iStart;
      log.i(id + ":iteration time:(" + iteration + ")" + diff + " gain:" + gain);
//      System.err.println(id + ":iteration time:(" + iteration + ")" + diff + " gain:" + gain);
      if (endTime > 0 && endTime < System.currentTimeMillis()) {
        log.i("Generation timed out");
        break;
      }
    }
    if (gain < threshold) log.i("gain under threshold (" + gain + " vs " + threshold + ")");
    generator.endSuite();
  }

  private boolean shouldRun(int gain, int iterations) {
    if (maxIterations > 0) {
      if (iterations >= maxIterations) return false;
    }
      return gain >= threshold;
  }

  private void updateRequirementsCoverage(TestCoverage suiteCoverage) {
    //finally, we need to update the coverage in the FSM to reflect the final pruned suite
    //the coverage in fsm is used by coverage reporters which is why we need this
    Requirements reqs = fsm.getRequirements();
    reqs.fillCoverage(suiteCoverage);
  }

  private void check() {
    if (osmoConfig.getFactory() instanceof SingleInstanceModelFactory) {
      System.out.println(MultiOSMO.ERROR_MSG);
    }
    if (config.getLengthWeight() > 0) {
      log.w("Length weight was defined as > 0, reset to 0.");
      //we do not use positive length weight as it would potentially go on for ever..
      config.setLengthWeight(0);
    }
    if (threshold < 1) log.w("Threshold is " + threshold + ", which is impossible to reach. Are you sure?");
  }

  private MainGenerator configure(long seed) {
    OSMOTester tester = new OSMOTester();
    tester.setConfig(osmoConfig);
    MainGenerator generator = tester.initGenerator(seed);
    osmoConfig.initialize(seed, tester.getFsm());
    generator.initSuite();
    generator.getSuite().setKeepTests(false);
    this.fsm = generator.getFsm();
    EndCondition endCondition = osmoConfig.getTestCaseEndCondition();
    endCondition.init(seed, fsm, osmoConfig);
    tester.setTestEndCondition(endCondition);
    config.validate(fsm);
    log.d("greedy configuration validated");
    return generator;
  }

  private void writeReport(CSVCoverageReport report, TestCoverage tc, int resultSize, int generationCount) {
    String summary = "summary\n";
    //we do not have the set of possible states or state pairs as those would require executing the "states" which greedy does not do..
    summary += tc.coverageString(fsm, possiblePairs, null, null, null, false);

    String totalCsv = report.report();
    totalCsv += summary + "\n";
    TestUtils.write(totalCsv, createReportPath());
    long end = System.currentTimeMillis();
    long diff = end - start;
    log.i("GreedyOptimizer " + id + " generated " + generationCount + " tests.");
    log.i("Resulting suite has " + resultSize + " tests. Generation time " + diff + " millis");
  }

  public String createReportPath() {
    String filename = id + "-scores.csv";
    return "osmo-output/" + midPath + "greedy-" + seed + "/" + filename;
  }

  /**
   * Same as createSortedSet(int howMany) but does not generate the tests, uses the given set as source instead.
   *
   * @param id The generator id (when several in parallel).
   * @param from The source set to pick from.
   * @param calculator For calculating coverage score.
   * @param max Maximum number of tests to include in results.
   * @return Greedily sorted suite of requested size.
   */
  public static List<TestCase> sortAndPrune(int id, List<TestCase> from, ScoreCalculator calculator, int max) {
    //this sort is here to ensure deterministic results (as far as sequence of steps and scores go..)
    //TODO: optimize the sort based on something other than full .tostring
    from.sort(new TestSorter());
    List<TestCase> suite = new ArrayList<>();

    //first we need to clone the coverage so we can reuse it later since first loop removes items from it
    for (TestCase test : from) {
      test.cloneCoverage();
    }
    int times = 0;
    TestCase best = null;
    TestCoverage bestCoverage = null;
    while (from.size() > 0) {
      int bestScore = 0;
      TestCase found = null;
      for (TestCase test : from) {
        TestCoverage tc = test.getCoverage();
        if (best != null) tc.removeAll(bestCoverage);
        int score = calculator.calculateScore(tc);
        if (score > bestScore) {
          bestScore = score;
          found = test;
        }
        times++;
      }
      if (found == null) {
        //no more gains found in coverage
        break;
      }
      best = found;
      bestCoverage = best.getCoverage();
//      System.out.println("best:"+bestScore);
      //TODO: check if possible to optimize this removal
      from.remove(best);
      suite.add(best);
      //if max length for suite defined, we do not go beyond that
      if (max > 0 && suite.size() >= max) break;
    }
    int steps = 0;
    //loop all and not just the results, as sometimes the listeners use the old ones (the GUI does at this writing)
    for (TestCase test : from) {
      //here we switch to the original coverage
      test.switchToClonedCoverage();
      steps += test.getCoverage().getTotalSteps();
    }
    for (TestCase test : suite) {
      //here we switch to the original coverage
      test.switchToClonedCoverage();
      steps += test.getCoverage().getTotalSteps();
    }
    log.i(id+":loops in sort:" + times + ", tests:" + suite.size() + ", steps:" + steps);
    return suite;
  }

  public FSM getFsm() {
    return fsm;
  }

  public Collection<String> getPossiblePairs() {
    return possiblePairs;
  }

  public void addIterationListener(IterationListener listener) {
    listeners.add(listener);
  }

  /**
   * NOTE: greedy with limited max size can actually get smaller gain over time.
   * Mostly this seems to be due to achieving higher overall score in all but when limiting the size,
   * the ordering when picking highest first seems to produce a case where adding the third one stops
   * from adding a full set of 5 that would score higher. Instead, these would come later..
   */
  public void disableThreshold() {
    this.threshold = Integer.MIN_VALUE;
  }

  public void setSubStatus(boolean subStatus) {
    this.subStatus = subStatus;
  }

  public boolean isSubStatus() {
    return subStatus;
  }
}
