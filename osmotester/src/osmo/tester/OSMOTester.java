package osmo.tester;

import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.GenerationListenerList;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.algorithm.GenerationAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.ProbabilityCondition;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.parser.MainParser;

import java.util.ArrayList;
import java.util.Collection;

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
  private EndCondition suiteStrategy = new ProbabilityCondition(0.95d);
  /** When do we stop generating individual tests and start a new one? */
  private EndCondition testStrategy = new ProbabilityCondition(0.9d);
  /** The algorithm to traverse the test model to generate test steps. */
  private GenerationAlgorithm algorithm = new RandomAlgorithm();
  /** Listeners to be notified about test generation events. */
  private GenerationListenerList listeners = new GenerationListenerList();

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
    MainGenerator generator = new MainGenerator();
    generator.setAlgorithm(algorithm);
    generator.setSuiteStrategy(suiteStrategy);
    generator.setTestStrategy(testStrategy);
    generator.setListeners(listeners);
    MainParser parser = new MainParser();
    FSM fsm = parser.parse(modelObjects);
    generator.generate(fsm);
    System.out.println("generated " + fsm.getTestSuite().getHistory().size() + " tests.\n");
    System.out.println(fsm.getRequirements().printCoverage());
  }

  /**
   * Add a condition for stopping the generation of whole test suite.
   *
   * @param condition The new condition to stop overall suite generation.
   */
  public void addSuiteEndCondition(EndCondition condition) {
    this.suiteStrategy = condition;
  }

  /**
   * Add a condition for stopping the generation of individual test cases.
   *
   * @param condition The new condition to stop individual test generation.
   */
  public void addTestEndCondition(EndCondition condition) {
    this.testStrategy = condition;
  }

  /**
   * Set the algorithm for test generation.
   *
   * @param algorithm New test generation algorithm.
   */
  public void setAlgorithm(GenerationAlgorithm algorithm) {
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
}
