package osmo.tester;

import osmo.common.log.Logger;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.filter.StepFilter;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.Requirements;

import java.util.logging.Level;

/**
 * The main class for initiating the MBT tool.
 * <p/>
 * Create the model object using the annotations from osmo.tester.annotation package.
 * Set test generation stop strategies for both the overall test suite and individual test cases.
 * Set the algorithm for test generation.
 * Invoke generate().
 *
 * @author Teemu Kanstren
 */
public class OSMOTester {
  private static Logger log = new Logger(OSMOTester.class);
  /** The parsed model for test generation. */
  private FSM fsm = null;
  /** Configuration for test generation. */
  private OSMOConfiguration config = new OSMOConfiguration();
  /** For generating the tests. */
  private MainGenerator generator = null;
  /** Do we print model coverage in the end? */
  private boolean printCoverage = true;

  /**
   * Create the tester with the initialized test model object.
   *
   * @param modelObject The model object defined using the OSMOTester annotations.
   */
  public OSMOTester(Object modelObject) {
    addModelObject(modelObject);
  }

  /** A constructor for use with addModelObject() method. */
  public OSMOTester() {
  }

  public void setPrintCoverage(boolean printCoverage) {
    this.printCoverage = printCoverage;
  }

  /**
   * Adds a new model object, to be composed by OSMO to a single internal model along with other model objects.
   *
   * @param modelObject The model object (with OSMO annotations) to be added.
   */
  public void addModelObject(Object modelObject) {
    config.addModelObject(modelObject);
  }
  
  public void setModelFactory(ModelFactory factory) {
    config.setFactory(factory);
  }

  /**
   * Adds a model object with a given prefix, allowing the same object class to be re-used with different configuration
   * where the names of transitions (test steps), guards and other elements is preceded by the given prefix.
   *
   * @param prefix      The model prefix.
   * @param modelObject The model object itself.
   */
  public void addModelObject(String prefix, Object modelObject) {
    config.addModelObject(prefix, modelObject);
  }

  /** Invoke this to perform actual test generation from the given model, with the given algorithms and strategies. */
  public void generate(long seed) {
    log.debug("generator starting up");
    generator = initGenerator(seed);
    generator.generate();
    if (!printCoverage) return;
    TestSuite suite = generator.getSuite();
    System.out.println("generated " + suite.getFinishedTestCases().size() + " tests.\n");
    TestCoverage tc = new TestCoverage(suite.getAllTestCases());
    String coverage = tc.coverageString(fsm, generator.getPossibleStepPairs(), null, null, null, false);
    System.out.println(coverage);
    Requirements requirements = suite.getRequirements();
    if (!requirements.isEmpty()) {
      System.out.println();
      System.out.println(requirements.printCoverage());
    }
  }

  public MainGenerator initGenerator(long seed) {
    TestSuite suite = new TestSuite();
    MainGenerator generator = new MainGenerator(seed, suite, config);
    fsm = generator.getFsm();
//    config.initialize(seed, fsm);
    return generator;
  }

  public TestSuite getSuite() {
    return generator.getSuite();
  }

  public FSM getFsm() {
    if (fsm == null) {
      initGenerator(0);
    }
    return fsm;
  }

  /**
   * Add a condition for stopping the generation of whole test suite.
   *
   * @param condition The new condition to stop overall suite generation.
   */
  public void setSuiteEndCondition(EndCondition condition) {
    config.setSuiteEndCondition(condition);
  }

  /**
   * Add a condition for stopping the generation of individual test cases.
   *
   * @param condition The new condition to stop individual test generation.
   */
  public void setTestEndCondition(EndCondition condition) {
    config.setTestEndCondition(condition);
  }

  /**
   * Set the algorithm for test generation.
   *
   * @param algorithm New test generation algorithm.
   */
  public void setAlgorithm(FSMTraversalAlgorithm algorithm) {
    config.setAlgorithm(algorithm);
  }

  /**
   * If true, debug information is printed to console and file.
   *
   * @param debug True for debug information, false for no such information.
   */
  public void setDebug(boolean debug) {
    if (debug) Logger.consoleLevel = Level.FINE;
     else Logger.consoleLevel = Level.INFO;
  }

  public void addListener(GenerationListener listener) {
    config.addListener(listener);
  }

  public void addFilter(StepFilter filter) {
    config.addFilter(filter);
  }

  public OSMOConfiguration getConfig() {
    return config;
  }

  public void setConfig(OSMOConfiguration config) {
    this.config = config;
  }
}
