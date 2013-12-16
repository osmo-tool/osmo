package osmo.tester.explorer;

import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.ReflectiveModelFactory;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.optimizer.CSVCoverageReport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The main class for using OSMO Explorer.
 * OSMO Explorer runs the OSMO Tester test generator but using a specific exploration algorithm.
 * The algorithm explores N steps into the future before choosing the next step to concretely execute.
 * Each path is scored according to given coverage score criteria, and the highest scoring is chosen as the next step.
 * That is, the first step on the highest scoring path. 
 * If several paths score the same, the first step on the path that is the fastest to score the most is chosen.
 *
 * @author Teemu Kanstren
 */
public class OSMOExplorer {
  /** The test generator used to do the actual test generation based on the exploration results. */
  private OSMOTester osmo = new OSMOTester();
  /** The algorithm used by OSMO Tester to explore the future. */
  private ExplorerAlgorithm algorithm;
  /** Classes to create model objects if a factory is not provided. */
  private Collection<Class> classes = new ArrayList<>();
  /** Listeners for the exploration, only called for actual generation not explored paths. */
  private Collection<GenerationListener> listeners = new ArrayList<>();
  /** Underlying OSMO Configuration for the actual generator. */
  private OSMOConfiguration osmoConfig = new OSMOConfiguration();
  private ExplorationConfiguration config;
  /** Identifier for next greedy optimizer if several are created. */
  private static int nextId = 1;
  /** The identifier for this optimizer. */
  private int id = nextId++;

  public void addModelClass(Class modelClass) {
    this.classes.add(modelClass);
  }

  /**
   * Access to the configuration of the underlying test generator.
   * Not that many parameters are overwritten by the explorer when exploration is started.
   * The main parameter to set is the test case end condition.
   * 
   * @return The configuration for the underlying generator.
   */
  public OSMOConfiguration getOSMOConfig() {
    return osmoConfig;
  }

  /**
   * Adds a listener to the underlying generator, to be notified about concretely executed steps.
   * 
   * @param listener to add.
   */
  public void addListener(GenerationListener listener) {
    listeners.add(listener);
  }

  /**
   * Runs exploration according to the given configuration.
   *
   * @param config The configuration to use.
   */
  public void explore(ExplorationConfiguration config) {
    this.config = config;
    long start = System.currentTimeMillis();
    configureGenerator();

    System.out.println("Starting exploration with " + config.getParallelism() + " parallel processes.");
    //actual generation magic happens here
    osmo.generate(config.getSeed());

    createScoreReport();
    //here we write the trace report.. =sequence of steps concretely executed
    OSMOTester.writeTrace(createReportPath()+"exploration", osmo.getSuite().getAllTestCases(), config.getSeed(), osmoConfig);

    double seconds = calculateTime(start);
    System.out.println("Generation time: " + seconds + "s.");
  }

  private double calculateTime(long start) {
    long end = System.currentTimeMillis();
    long diff = end - start;
    return (double) (diff / 1000);
  }

  /**
   * Creates a report for exploration/generation.
   * Prints information such as covered elements, achieved score, used generation parameters.
   */
  private void createScoreReport() {
    boolean printAll = config.isPrintAll();
    Map<String, Collection<String>> possibleValues = algorithm.getPossibleValues();
    Collection<String> possibleStepPairs = algorithm.getPossibleStepPairs();
    Map<String, Collection<String>> possibleStatePairs = algorithm.getPossibleStatePairs();
    Map<String, Collection<String>> possibleStates = algorithm.getPossibleStates();
    List<TestCase> allTests = osmo.getSuite().getAllTestCases();
    TestCoverage tc = new TestCoverage(allTests);
    ScoreCalculator sc = new ScoreCalculator(config);
    System.out.println("Generated " + allTests.size() + " tests. Achieved score " + sc.calculateScore(tc));

    CSVCoverageReport report = new CSVCoverageReport(sc);
    String summary = "summary\n";
    summary += tc.coverageString(osmo.getFsm(), possibleStepPairs, possibleValues, possibleStates, possibleStatePairs, printAll);
    System.out.println(summary);
    report.process(allTests);
    String totalCsv = report.report();
    totalCsv += summary + "\n";
    TestUtils.write(totalCsv, createFullReportPath());
  }
  
  public String createFullReportPath() {
    return createReportPath() + id + "-scores.csv";
  }
  
  private String createReportPath() {
    return "osmo-output/expl-" + config.getSeed() + "-" + config.getDepth() + "/";
  }

  /**
   * Configures the underlying test generator with the exploration configuration.
   */
  private void configureGenerator() {
    osmo.setPrintCoverage(false);
    if (classes.size() > 0) {
      ReflectiveModelFactory factory = new ReflectiveModelFactory();
      for (Class modelClass : classes) {
        factory.addModelClass(modelClass);
      }
      config.setFactory(factory);
    }
    config.fillOSMOConfiguration(osmoConfig);
    osmoConfig.setExploring(true);
    for (GenerationListener listener : listeners) {
      osmoConfig.addListener(listener);
    }
    osmo.setConfig(osmoConfig);
    algorithm = new ExplorerAlgorithm(config);
    osmo.setAlgorithm(algorithm);
  }

  public ExplorerAlgorithm getAlgorithm() {
    return algorithm;
  }

  public TestSuite getSuite() {
    return osmo.getSuite();
  }
}
