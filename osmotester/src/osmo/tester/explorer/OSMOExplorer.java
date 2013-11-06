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
import osmo.tester.optimizer.CSVReport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The main class for using OSMO Explorer. OSMO Explorer is the tool that simultaneously runs a set of paths
 * while generating test cases and chooses the next step on the path that scores the best on given coverage criteria.
 * The exploration is re-executed for each step up to the given depth.
 * This is more for an online type approach.
 * Another option more for an offline approach would be to fully explore the whole suite at once,
 * which would be a future addition.
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
  /** Configuration of exporation.. */
  private ExplorationConfiguration config = null;
  /** Identifier for next greedy optimizer if several are created. */
  private static int nextId = 1;
  /** The identifier for this optimizer. */
  private int id = nextId++;

  public void addModelClass(Class modelClass) {
    this.classes.add(modelClass);
  }

  public OSMOConfiguration getOSMOConfig() {
    return osmoConfig;
  }

  public void addListener(GenerationListener listener) {
    listeners.add(listener);
  }

  /**
   * Runs exploration according to the given configuration.
   *
   * @param config The configuration to use.
   */
  public void explore(ExplorationConfiguration config) {
    long start = System.currentTimeMillis();
    configureWith(config);

    System.out.println("Starting exploration with " + config.getParallelism() + " parallel processes.");
    osmo.generate(config.getSeed());

    String path = "osmo-output/expl-" + config.getSeed() + "-" + config.getDepth() + "/";
    createScoreReport(path, config);
    //here we write the trace report..
    OSMOTester.writeTrace(path+"exploration", osmo.getSuite().getAllTestCases(), config.getSeed(), osmoConfig);

    double seconds = calculateTime(start);
    System.out.println("Generation time: " + seconds + "s.");
  }

  private double calculateTime(long start) {
    long end = System.currentTimeMillis();
    long diff = end - start;
    return (double) (diff / 1000);
  }

  private void createScoreReport(String path, ExplorationConfiguration config) {
    boolean printAll = config.isPrintAll();
    Map<String, Collection<String>> possibleValues = algorithm.getPossibleValues();
    Collection<String> possibleStepPairs = algorithm.getPossibleStepPairs();
    Map<String, Collection<String>> possibleStatePairs = algorithm.getPossibleStatePairs();
    Map<String, Collection<String>> possibleStates = algorithm.getPossibleStates();
    List<TestCase> allTests = osmo.getSuite().getAllTestCases();
    TestCoverage tc = new TestCoverage(getSuite().getFinishedTestCases());
    ScoreCalculator sc = new ScoreCalculator(config);
    System.out.println("Generated " + allTests.size() + " tests. Achieved score " + sc.calculateScore(tc));

    CSVReport report = new CSVReport(sc);
    String summary = "summary\n";
    summary += tc.coverageString(osmo.getFsm(), possibleStepPairs, possibleValues, possibleStates, possibleStatePairs, printAll);
    System.out.println(summary);
    report.process(allTests);
    String totalCsv = report.report();
    totalCsv += summary + "\n";
    TestUtils.write(totalCsv, path + id + "-scores.csv");
  }

  private void configureWith(ExplorationConfiguration config) {
    this.config = config;
    osmo.setPrintCoverage(false);
    if (classes.size() > 0) {
      ReflectiveModelFactory factory = new ReflectiveModelFactory();
      for (Class modelClass : classes) {
        factory.addModelClass(modelClass);
      }
      config.setFactory(factory);
    }
    config.fillOSMOConfiguration(osmoConfig);
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
