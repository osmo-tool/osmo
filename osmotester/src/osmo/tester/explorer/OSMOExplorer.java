package osmo.tester.explorer;

import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.SimpleModelFactory;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.ArrayList;
import java.util.Collection;
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
  private Collection<GenerationListener> listeners = new ArrayList<>();
  private OSMOConfiguration osmoConfig = new OSMOConfiguration();
  
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
    osmo.setPrintCoverage(false);
    long start = System.currentTimeMillis();
    if (classes.size() > 0) {
      SimpleModelFactory factory = new SimpleModelFactory();
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
    System.out.println("Starting exploration with "+config.getParallelism()+" parallel processes.");
    osmo.generate(config.getSeed());
    
    TestCoverage tc = new TestCoverage(getSuite().getFinishedTestCases());
    boolean printAll = config.isPrintAll();
    Map<String,Collection<String>> possibleValues = algorithm.getPossibleValues();
    Collection<String> possibleStepPairs = algorithm.getPossibleStepPairs();
    Map<String,Collection<String>> possibleStatePairs = algorithm.getPossibleStatePairs();
    Map<String,Collection<String>> possibleStates = algorithm.getPossibleStates();
    String summary = tc.coverageString(osmo.getFsm(), possibleStepPairs, possibleValues, possibleStates, possibleStatePairs, printAll);
    System.out.println(summary);
    long end = System.currentTimeMillis();
    long diff = end -start;
    double seconds = diff / 1000;
    System.out.println("Generation time: "+seconds+"s.");
  }
  
  //TODO: explorationissa tarttee näköjään rajoitteen ettei beforesuite alusta mitään instanssikohtaista
  public ExplorerAlgorithm getAlgorithm() {
    return algorithm;
  }

  public TestSuite getSuite() {
    return osmo.getSuite();
  }
}
