package osmo.tester;

import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.GenerationListenerList;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.parser.MainParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * The main class for initiating the MBT tool.
 *
 * Create the model object using the annotations from osmo.tester.annotation package.
 * Set test generation stop strategies for both the overall test suite and individual test cases.
 * Set the algorithm for test generation.
 * Invoke generate().
 *
 * @author Teemu Kanstren
 */
public class OSMOTester {
  /** The set of test model objects, given by the user. */
  private final Collection<Object> modelObjects = new ArrayList<Object>();
  /** When do we stop generating the overall test suite? (stopping all test generation)*/
  private Collection<EndCondition> suiteEndConditions = new ArrayList<EndCondition>();
  /** When do we stop generating individual tests and start a new one? */
  private Collection<EndCondition> testCaseEndConditions = new ArrayList<EndCondition>();
  /** The algorithm to traverse the test model to generate test steps. */
  private FSMTraversalAlgorithm algorithm;
  /** Listeners to be notified about test generation events. */
  private GenerationListenerList listeners = new GenerationListenerList();
  /** The set of generated tests. */
  private TestSuite suite = new TestSuite();
  /** The parsed model for test generation. */
  private FSM fsm = null;

  /**
   * Create the tester with the initialized test model object.
   *
   * @param modelObject The model object defined using the OSMOTester annotations.
   */
  public OSMOTester(Object modelObject) {
    modelObjects.add(modelObject);
  }

  /**
   * A constructor for use with addModelObject() method.
   */
  public OSMOTester() {
  }

  /**
   * Adds a new model object, to be composed by OSMO to a single internal model along with other model objects.
   *
   * @param modelObject The model object (with OSMO annotations) to be added.
   */
  public void addModelObject(Object modelObject) {
    modelObjects.add(modelObject);
  }

  /**
   * Invoke this to perform actual test generation from the given model, with the given algorithms and strategies.
   */
  public void generate() {
    MainGenerator generator = new MainGenerator(suite);
    if (algorithm == null) {
      //we do this here to avoid initializing from TestUtils.getRandom() before user calls setRandom() in this class
      algorithm = new RandomAlgorithm();
    }
    generator.setAlgorithm(algorithm);
    if (suiteEndConditions.size() == 0) {
      addSuiteEndCondition(new Probability(0.05d));
    }
    if (testCaseEndConditions.size() == 0) {
      addTestEndCondition(new Probability(0.1d));
    }
    generator.setSuiteEndConditions(suiteEndConditions);
    generator.setTestCaseEndConditions(testCaseEndConditions);
    generator.setListeners(listeners);
    MainParser parser = new MainParser(suite);
    fsm = parser.parse(modelObjects);
    generator.generate(fsm);
    System.out.println("generated " + suite.getTestCases().size() + " tests.\n");
    System.out.println(fsm.getRequirements().printCoverage());
  }

  public TestSuite getSuite() {
    return suite;
  }

  public FSM getFsm() {
    return fsm;
  }

  /**
   * Add a condition for stopping the generation of whole test suite.
   *
   * @param condition The new condition to stop overall suite generation.
   */
  public void addSuiteEndCondition(EndCondition condition) {
    suiteEndConditions.add(condition);
  }

  /**
   * Add a condition for stopping the generation of individual test cases.
   *
   * @param condition The new condition to stop individual test generation.
   */
  public void addTestEndCondition(EndCondition condition) {
    testCaseEndConditions.add(condition);
  }

  /**
   * Set the algorithm for test generation.
   *
   * @param algorithm New test generation algorithm.
   */
  public void setAlgorithm(FSMTraversalAlgorithm algorithm) {
    this.algorithm = algorithm;
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
    listeners.addListener(listener);
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
   * @param random The new random number generator.
   */
  public void setRandom(Random random) {
    TestUtils.setRandom(random);
  }

}
