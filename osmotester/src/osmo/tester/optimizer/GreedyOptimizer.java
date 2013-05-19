package osmo.tester.optimizer;

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
import osmo.tester.reporting.coverage.CoverageMetric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Generates test cases and greedily optimizes the resulting test suite with regards to coverage criteria as
 * given in an {@link osmo.tester.coverage.ScoreConfiguration}.
 * Resulting suite is optimised so the first test in the given set has the highest overall fitness values,
 * the second one adds
 * most fitness to that one as evaluated in terms of single test cases, the third adds the most to these two,
 * and so on.
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
  /** An alternative to providing a model object factory is to give the set of classes used to instanciate the objects. */
  private final Collection<Class> modelClasses = new ArrayList<>();
  private ModelFactory factory = null;
  /** Identifier for next greedy optimizer if several are created. */
  private static int nextId = 1;
  /** The identifier for this optimizer. */
  private int id = nextId++;
  /** Used to calculate coverage scores for different tests and suites. */
  private final ScoreCalculator scoreCalculator;
  private final EndCondition endCondition;
  private int threshold = 1;
  private long timeout = -1;
  private Collection<String> possiblePairs = new HashSet<>();

  /**
   * Uses a default population size of 100.
   * 
   * @param configuration For scoring the search.
   */
  public GreedyOptimizer(ScoreConfiguration configuration, EndCondition endCondition) {
    this(configuration, 1000, endCondition);
  }

  /**
   * Constructor.
   * 
   * @param configuration For scoring the search.
   * @param populationSize How many tests to create in an iteration.
   */
  public GreedyOptimizer(ScoreConfiguration configuration, int populationSize, EndCondition endCondition) {
    this.config = configuration;
    this.populationSize = populationSize;
    this.scoreCalculator = new ScoreCalculator(configuration);
    this.endCondition = endCondition;
  }

  public void addModelClass(Class modelClass) {
    this.modelClasses.add(modelClass);
  }
  
  public void setFactory(ModelFactory factory) {
    this.factory = factory;
  }

  public void setThreshold(int threshold) {
    if (threshold < 1) {
      throw new IllegalArgumentException("Threshold must be minimum of 1, was "+threshold);
    }
    this.threshold = threshold;
  }

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
    OSMOTester tester = new OSMOTester();
    long seed = OSMOConfiguration.getSeed();
    if (config.getLengthWeight() > 0) {
      log.warn("Length weight was defined as > 0, reset to 0.");
      //we do not use length weight as it would potentially go on for ever..
      config.setLengthWeight(0);
    }
    if (factory != null) {
      log.debug("Using factory from configuration");
      tester.setModelFactory(factory, OSMOConfiguration.getSeed());
    } else if (modelClasses.size() > 0) {
      log.debug("Creating factory for given model classes");
      SimpleModelFactory factory = new SimpleModelFactory(modelClasses.toArray(new Class[modelClasses.size()]));
      tester.setModelFactory(factory, seed);
    } else {
      throw new IllegalStateException("No model factory found.");
    }
    MainGenerator generator = tester.initGenerator();
    generator.initSuite();
//    TestCoverage generatorCoverage = generator.getSuite().getCoverage();
    tester.setTestEndCondition(endCondition);
    this.fsm = generator.getFsm();
    config.validate(fsm);
    log.debug("greedy configuration validated");

    List<TestCase> suite = new ArrayList<>();
//    TestCoverage suiteCoverage = new TestCoverage(config);
    long start = System.currentTimeMillis();
    int iteration = 0;
    int gain = Integer.MAX_VALUE;
    int previousScore = 0;
    String csv1 = "cumulative coverage per test\n";
    String csv2 = "gained coverage per test\n";
    String csv3 = "number of tests in suite\n";
    String csv4 = "total score\n";
    if (timeout > 0) {
      //timeout is given in seconds so we multiple by 1000 to get milliseconds
      timeout = System.currentTimeMillis()+timeout*1000;
    }
    while (gain >= threshold) {
      log.info(id + ":iteration " + iteration);
      iteration++;
      for (int i = 0 ; i < populationSize ; i++) {
        log.debug("creating test case " + i);
        TestCase testCase = generator.nextTest();
        suite.add(testCase);
      }
      suite = sortAndPrune(suite);
      csv1 += csvForCoverage(suite);
      csv2 += csvForGain(suite);
      csv3 += csvNumberOfTests(suite);
      csv4 += csvTotalScores(suite);
      TestCoverage suiteCoverage = new TestCoverage(suite);
      int score = scoreCalculator.calculateScore(suiteCoverage);
      gain = score - previousScore;
      previousScore = score;
      if (timeout > 0 && timeout < System.currentTimeMillis()) {
        System.out.println("Generation timed out");
        log.debug("Generation timed out");
        break;
      }
    }
    generator.endSuite();
    this.possiblePairs = generator.getPossiblePairs();
    TestCoverage suiteCoverage = new TestCoverage(suite);

    String summary = "summary\n";
    summary += suiteCoverage.coverageString(fsm, possiblePairs, null, null, false);
    
    String totalCsv = "";
    totalCsv += csv1+"\n";
    totalCsv += csv2+"\n";
    totalCsv += csv3+"\n";
    totalCsv += csv4+"\n";
    totalCsv += summary+"\n";
    writeFile(id+"-scores.csv", totalCsv);
    long end = System.currentTimeMillis();
    long diff = end - start;
    log.info("GreedyOptimizer "+id+" generated "+(iteration*populationSize)+" tests.");
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

  public void writeFile(String name, String content) {
    try {
      CoverageMetric.write(content, "osmo-output/"+name);
    } catch (IOException e) {
      log.error("Failed to write coverage data to file", e);
    }
  }

  protected String csvForCoverage(Collection<TestCase> tests) {
    String csv = "";
    TestCoverage tc = new TestCoverage();
    for (TestCase test : tests) {
      tc.addTestCoverage(test);
      csv += scoreCalculator.calculateScore(tc) + "; ";
    }
    csv += "\n";
    return csv;
  }
  
  protected String csvNumberOfTests(Collection<TestCase> tests) {
    String csv = "";
    csv += tests.size();
    csv += "\n";
    return csv;
  }

  protected String csvTotalScores(Collection<TestCase> tests) {
    String csv = "";
    TestCoverage tc = new TestCoverage();
    for (TestCase test : tests) {
      tc.addTestCoverage(test);
    }
    csv += scoreCalculator.calculateScore(tc);
    csv += "\n";
    return csv;
  }

  protected String csvForGain(Collection<TestCase> tests) {
    String csv = "";
    TestCoverage tc = new TestCoverage();
    for (TestCase test : tests) {
      int old = scoreCalculator.calculateScore(tc);
      tc.addTestCoverage(test);
      int now = scoreCalculator.calculateScore(tc);
      int gain = now - old;
      csv += gain + "; ";
    }
    csv += "\n";
    return csv;
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
    TestCoverage suiteCoverage = new TestCoverage();
    int count = from.size();
    for (int i = 0 ; i < count ; i++) {
      int bestFitness = 0;
      TestCase best = null;
      for (TestCase test : from) {
        int fitness = scoreCalculator.addedScoreFor(suiteCoverage, test);
        if (fitness > bestFitness) {
          bestFitness = fitness;
          best = test;
        }
      }
      if (best == null) {
        //no more gains found in coverage
        break;
      }
      from.remove(best);
      suite.add(best);
      suiteCoverage.addTestCoverage(best);
    }
    return suite;
  }

  public FSM getFsm() {
    return fsm;
  }

  public Collection<String> getPossiblePairs() {
    return possiblePairs;
  }
}
