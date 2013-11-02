package osmo.tester.optimizer;

import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.SimpleModelFactory;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.FSM;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.Requirements;
import osmo.tester.model.data.ValueSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
  private static Logger log = new Logger(GreedyOptimizer.class);
  /** Defines weights for different coverage requirements to optimize for. */
  private final ScoreConfiguration config;
  /** The test model. */
  private FSM fsm = null;
  /** The number of tests to generate in an iteration. */
  private final int populationSize;
  /** An alternative to providing a model object factory is to give the set of classes used to instantiate the objects. */
  private final Collection<Class> modelClasses = new ArrayList<>();
  /** For creating model objects. */
  private ModelFactory factory = null;
  /** Identifier for next greedy optimizer if several are created. */
  private static int nextId = 1;
  /** The identifier for this optimizer. */
  private int id = nextId++;
  /** Used to calculate coverage scores for different tests and suites. */
  private final ScoreCalculator scoreCalculator;
  /** End condition for the test generator (for test cases only, not suite). */
  private final EndCondition endCondition;
  /** How much does an iteration need to gain in score to go for another iteration? Defaults to 1. */
  private int threshold = 1;
  /** Seconds until the search times out. Timeout is checked between iterations and refers to how long overall generation progresses. */
  private long timeout = -1;
  /** Passed to the generator. */
  private boolean failOnError = true;
  /** Seed for the generator. */
  private final long seed;
  /** For tracking all the path options encountered. */
  private Collection<String> possiblePairs = new HashSet<>();
  /** The generator to use. */
  private OSMOTester tester = new OSMOTester();

  /**
   * Uses a default population size of 1000.
   *
   * @param configuration For scoring the search.
   * @param endCondition  Test end condition.
   * @param seed          Randomization seed for generation.
   */
  public GreedyOptimizer(ScoreConfiguration configuration, EndCondition endCondition, long seed) {
    //TODO: remove endcondition and rely on config
    this(configuration, 1000, endCondition, seed);
  }

  /**
   * @param configuration  For scoring the search.
   * @param populationSize How many tests to create in an iteration.
   * @param endCondition   Test end condition.
   * @param seed           Randomization seed for generation.
   */
  public GreedyOptimizer(ScoreConfiguration configuration, int populationSize, EndCondition endCondition, long seed) {
    this.config = configuration;
    this.populationSize = populationSize;
    this.scoreCalculator = new ScoreCalculator(configuration);
    this.endCondition = endCondition;
    this.seed = seed;
  }
  
  //TODO:test this
  public void setOSMOConfiguration(OSMOConfiguration config) {
    tester.setConfig(config);
  }

  public OSMOConfiguration getConfiguration() {
    //TODO: remove all other setters and just rely on this
    return tester.getConfig();
  }

  public void addModelClass(Class modelClass) {
    this.modelClasses.add(modelClass);
  }

  public void setFactory(ModelFactory factory) {
    this.factory = factory;
  }

  /**
   * You can set any threshold you like. Zero or less means going on forever. Timeout is recommended in such case.
   *
   * @param threshold Required added coverage score for iteration to continue with another iteration.
   */
  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }

  /** @param timeout Generation timeout in seconds. */
  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  /**
   * Performs a search following the defined search configuration.
   * Provides a sorted list of test cases, where the one with highest fitness is first, one that
   * adds most to this is second, one that adds most to those two is third and so on.
   * The set of tests is chosen by first generating a number of tests equals to populationSize.
   * From this overall set, the subset that gives some added coverage is returned.
   * If a test adds no coverage to the overall set, it is not returned.
   *
   * @return The sorted set of test cases, with requested number of tests.
   */
  public List<TestCase> search() {
    if (config.getLengthWeight() >= 0) {
      log.warn("Length weight was defined as >= 0, reset to -1.");
      //we do not use positive length weight as it would potentially go on for ever..
      config.setLengthWeight(-1);
    }
    if (factory != null) {
      log.debug("Using factory from configuration");
      tester.setModelFactory(factory);
    } else if (modelClasses.size() > 0) {
      log.debug("Creating factory for given model classes");
      SimpleModelFactory factory = new SimpleModelFactory(modelClasses.toArray(new Class[modelClasses.size()]));
      tester.setModelFactory(factory);
    } else {
      throw new IllegalStateException("No model factory found.");
    }

    if (threshold < 1) log.warn("Threshold is " + threshold + ", which is impossible to reach. Are you sure?");

    OSMOConfiguration testerConfig = tester.getConfig();
    testerConfig.setFailWhenError(failOnError);
    MainGenerator generator = tester.initGenerator(seed);
    testerConfig.initialize(seed, tester.getFsm());
    generator.initSuite();
    this.fsm = generator.getFsm();
    endCondition.init(seed, fsm);
    tester.setTestEndCondition(endCondition);
    config.validate(fsm);
    log.debug("greedy configuration validated");

    List<TestCase> suite = new ArrayList<>();
    long start = System.currentTimeMillis();
    int iteration = 0;
    int gain = Integer.MAX_VALUE;
    int previousScore = 0;

    CSVReport report = new CSVReport(scoreCalculator);

    long endTime = -1;
    if (timeout > 0) {
      //timeout is given in seconds so we multiple by 1000 to get milliseconds
      endTime = System.currentTimeMillis() + timeout * 1000;
    }
    log.info("greedy "+id+" starting up");
    //TODO: change to also apprechiate smaller test suite
    while (gain >= threshold) {
      long iStart = System.currentTimeMillis();
      log.info(id + ":starting iteration " + iteration);
      iteration++;
      for (int i = 0 ; i < populationSize ; i++) {
        log.debug("creating test case " + i);
        TestCase testCase = generator.nextTest();
        suite.add(testCase);
      }
      suite = sortAndPrune(suite);
      //TODO: check why is this always processed?
      report.process(suite);
      TestCoverage suiteCoverage = new TestCoverage(suite);

      int score = scoreCalculator.calculateScore(suiteCoverage);
      gain = score - previousScore;
      previousScore = score;
      if (endTime > 0 && endTime < System.currentTimeMillis()) {
        log.info("Generation timed out");
        break;
      }
      long diff = System.currentTimeMillis() - iStart;
      log.info(id + ":iteration time:(" + iteration + ")" + diff + " gain:" + gain);
    }
    if (gain < threshold) log.info("gain under threshold (" + gain + " vs " + threshold + ")");

    generator.endSuite();
    this.possiblePairs = generator.getPossibleStepPairs();
    TestCoverage suiteCoverage = new TestCoverage(suite);

    String summary = "summary\n";
    //we do not have the set of possible states or state pairs as those would require executing the "states" which greedy does not do..
    summary += suiteCoverage.coverageString(fsm, possiblePairs, null, null, null, false);

    String totalCsv = report.report();
    totalCsv += summary + "\n";
    writeFile(id + "-scores.csv", totalCsv);
    long end = System.currentTimeMillis();
    long diff = end - start;
    log.info("GreedyOptimizer " + id + " generated " + (iteration * populationSize) + " tests.");
    log.info("Resulting suite has " + suite.size() + " tests. Generation time " + diff + " millis");

    //finally, we need to update the coverage in the FSM to reflect the final pruned suite
    //the coverage in fsm is used by coverage reporters which is why we need this
    Requirements reqs = fsm.getRequirements();
    reqs.clearCoverage();
    Collection<String> coveredReqs = suiteCoverage.getRequirements();
    for (String req : coveredReqs) {
      reqs.covered(req);
    }
    return suite;
  }

  /**
   * Writes given data to a file. Filename has the generation seed added to it.
   *
   * @param name    The filename. Ends up as "osmo-output-<seed>/name".
   * @param content The data to write.
   */
  public void writeFile(String name, String content) {
    TestUtils.write(content, "osmo-output-" + seed + "/" + name);
  }

  /**
   * Same as createSortedSet(int howMany) but does not generate the tests, uses the given set as source instead.
   *
   * @param from The source set to pick from.
   * @return Greedily sorted suite of requested size.
   */
  public List<TestCase> sortAndPrune(List<TestCase> from) {
    //this sort is here to ensure deterministic results (as far as sequence of steps and scores go..)
    Collections.sort(from, new TestSorter());
    List<TestCase> suite = new ArrayList<>();
    String ATTR_NAME = "osmo.tester.sorter.temp.coverage";
    for (TestCase test : from) {
      //first create initial coverage for all
      TestCoverage tc = new TestCoverage(test);
      test.setAttribute(ATTR_NAME, tc);
    }

    int count = from.size();
    TestCoverage previous = new TestCoverage();
    for (int i = 0 ; i < count ; i++) {
      int bestScore = 0;
      TestCase best = null;
      for (TestCase test : from) {
        TestCoverage tc = (TestCoverage) test.getAttribute(ATTR_NAME);
        tc.removeAll(previous);
        int score = scoreCalculator.calculateScore(tc);
        if (score > bestScore) {
          bestScore = score;
          best = test;
        }
      }
      if (best == null) {
        //no more gains found in coverage
        break;
      }
      from.remove(best);
      suite.add(best);
      previous = (TestCoverage) best.getAttribute(ATTR_NAME);
    }
    return suite;
  }

  public FSM getFsm() {
    return fsm;
  }

  public Collection<String> getPossiblePairs() {
    return possiblePairs;
  }

  public void setFailOnError(boolean failOnError) {
    this.failOnError = failOnError;
  }

  public static void main(String[] args) {
    int[] vs = new int[]{43, 44, 55, 100, 124, 171, 181, 234, 288, 315, 350, 353, 420, 438, 441, 468, 562, 582, 600, 668, 672, 726, 762, 814, 824, 896, 922, 1022, 1074, 1083, 1118, 1193, 1203, 1208, 1247, 1272, 1324, 1340, 1346, 1406, 1496, 1510, 1516, 1532, 1565, 1574, 1593, 1672, 1697, 1718, 1739, 1765, 1766, 1806, 1846, 1901, 1962, 1998, 2021, 2027, 2053, 2080, 2119, 2124, 2135, 2170, 2287, 2298, 2330, 2343, 2348, 2358, 2361, 2371, 2382, 2417, 2570, 2593, 2691, 2734, 2760, 2763, 2788, 2803, 2809, 2823, 2824, 2834, 2842, 2861, 2889, 2978, 3009, 3074, 3080, 3121, 3193, 3218, 3303, 3335, 3341, 3356, 3371, 3389, 3429, 3434, 3499, 3515, 3517, 3535, 3550, 3572, 3659, 3719, 3764, 3766, 3910, 3946, 3959, 3981, 3990, 3998, 4006, 4016, 4072, 4078, 4117, 4181, 4287, 4312, 4358, 4364, 4371, 4386, 4392, 4404, 4413, 4422, 4511, 4521, 4548, 4556, 4561, 4581, 4585, 4586, 4626, 4632, 4675, 4680, 4716, 4827, 4868, 4871, 4877, 4965, 5079, 5102, 5109, 5236, 5261, 5290, 5301, 5329, 5342, 5417, 5455, 5527, 5530, 5533, 5537, 5540, 5543, 5576, 5588, 5597, 5638, 5685, 5710, 5738, 5756, 5762, 5771, 5773, 5780, 5782, 5791, 5801, 5855, 5920, 5928, 5934, 5974, 5983, 5998, 6016, 6032, 6043, 6068, 6107, 6222, 6247, 6291, 6351, 6389, 6428, 6432, 6450, 6459, 6476, 6506, 6577, 6587, 6594, 6621, 6639, 6716, 6721, 6778, 6786, 6819, 6958, 6988, 7011, 7022, 7024, 7058, 7061, 7066, 7078, 7089, 7097, 7122, 7143, 7161, 7198, 7201, 7202, 7242, 7248, 7255, 7262, 7272, 7275, 7283, 7284, 7298, 7424, 7470, 7473, 7478, 7499, 7521, 7533, 7553, 7606, 7691, 7711, 7726, 7800, 7883, 7916, 7920, 7931, 7934, 7962, 7990, 8003, 8037, 8080, 8201, 8202, 8238, 8249, 8255, 8265, 8266, 8276, 8289, 8304, 8319, 8321, 8338, 8372, 8375, 8383, 8402, 8420, 8473, 8512, 8521, 8530, 8536, 8547, 8548, 8588, 8596, 8611, 8617, 8626, 8662, 8671, 8712, 8740, 8764, 8767, 8782, 8787, 8793, 8795, 8822, 8838, 8848, 8854, 8920, 8938, 8948, 8972, 8975, 9028, 9064, 9083, 9126, 9134, 9192, 9238, 9258, 9384, 9409, 9425, 9430, 9463, 9483};
    for (int v : vs) {
      ValueSet<String> names = new ValueSet<>("teemu", "paavo", "keijo");
      names.setSeed(v);
      System.out.println(names.next());
    }
  }
}
