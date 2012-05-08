package osmo.tester;

import osmo.common.TestUtils;
import osmo.common.log.Logger;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.filter.TransitionFilter;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.Requirements;
import osmo.tester.model.ScriptedValueProvider;
import osmo.tester.parser.MainParser;

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
  /** The parsed model for test generation. */
  private FSM fsm = null;
  /** Configuration for test generation. */
  private OSMOConfiguration config = new OSMOConfiguration();

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

  /**
   * Adds a new model object, to be composed by OSMO to a single internal model along with other model objects.
   *
   * @param modelObject The model object (with OSMO annotations) to be added.
   */
  public void addModelObject(Object modelObject) {
    config.addModelObject(modelObject);
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
  public void generate() {
    MainGenerator generator = initGenerator();
    generator.generate();
    System.out.println("generated " + fsm.getSuite().getFinishedTestCases().size() + " tests.\n");
    Requirements requirements = fsm.getRequirements();
    if (!requirements.isEmpty()) {
      System.out.println(requirements.printCoverage());
    }
  }

  public MainGenerator initGenerator() {
    //We do not initialize seed here since that would cause problems if model objects are initialized already with
    //TestUtils static methods (which use the seed, which would not be set..)
    //TestUtils.setSeed(config.getSeed());
    MainParser parser = new MainParser();
    fsm = parser.parse(config);
    MainGenerator generator = new MainGenerator(fsm, config);
    config.init(fsm);
//    generator.initSearchableInputs();
    return generator;
  }

  public TestSuite getSuite() {
    return fsm.getSuite();
  }

  public FSM getFsm() {
    if (fsm == null) {
      initGenerator();
    }
    return fsm;
  }

  /**
   * Add a condition for stopping the generation of whole test suite.
   *
   * @param condition The new condition to stop overall suite generation.
   */
  public void addSuiteEndCondition(EndCondition condition) {
    config.addSuiteEndCondition(condition);
  }

  /**
   * Add a condition for stopping the generation of individual test cases.
   *
   * @param condition The new condition to stop individual test generation.
   */
  public void addTestEndCondition(EndCondition condition) {
    config.addTestEndCondition(condition);
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
    Logger.debug = debug;
  }

  public void addListener(GenerationListener listener) {
    config.addListener(listener);
  }

  /**
   * Allows the user to define their own random number generator.
   * Typical use is to set a deterministic seed, as the standard approach in Java is to take
   * the seed from the system clock to also randomize that.
   * In practice this delegates the value to the {@link TestUtils} class, from which all the
   * functionality in OSMOTester uses it.
   * Note that if you use the functions from {@link TestUtils} yourself for any purposes
   * (as they are intended to be used for any purpose you like), the sequence of the next value
   * in algorithms etc. can change as they share the number generator.
   * This should generally not be a problem but, for example, in trying to create deterministic test cases
   * it may cause some confusion.
   *
   * @param seed The new random number generator.
   */
  public void setSeed(long seed) {
    config.setSeed(seed);
  }

  public void addFilter(TransitionFilter filter) {
    config.addFilter(filter);
  }

  public void setValueScripter(ScriptedValueProvider scripter) {
    config.setScripter(scripter);
  }

  public OSMOConfiguration getConfig() {
    return config;
  }

  public void setConfig(OSMOConfiguration config) {
    this.config = config;
  }
}
