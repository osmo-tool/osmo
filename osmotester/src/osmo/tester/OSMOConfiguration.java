package osmo.tester;

import osmo.common.TestUtils;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.GenerationListenerList;
import osmo.tester.generator.Observer;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.And;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.generator.filter.TransitionFilter;
import osmo.tester.model.FSM;
import osmo.tester.model.ScriptedValueProvider;
import osmo.tester.model.dataflow.SearchableInput;
import osmo.tester.model.dataflow.Text;
import osmo.tester.model.dataflow.ValueSet;
import osmo.tester.model.dataflow.serialization.Deserializer;
import osmo.tester.parser.ModelObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines configuration for test generation.
 *
 * @author Teemu Kanstren
 */
public class OSMOConfiguration {
  /** The set of test model objects, given by the user. */
  private final Collection<ModelObject> modelObjects = new ArrayList<>();
  /** When do we stop generating the overall test suite? (stopping all test generation). Ignored if junitLength is set. */
  private Collection<EndCondition> suiteEndConditions = new ArrayList<>();
  /** When do we stop generating individual tests and start a new one? */
  private Collection<EndCondition> testCaseEndConditions = new ArrayList<>();
  /** Set of filters to define when given transitions should not be considered for execution. */
  private Collection<TransitionFilter> filters = new ArrayList<>();
  /** The algorithm to traverse the test model to generate test steps. */
  private FSMTraversalAlgorithm algorithm;
  /** Should we fail then test generation if there is no enabled transition? Otherwise we just end the test. */
  private boolean failWhenNoWayForward = true;
  /** Should we fail test generation if an Exception is thrown? */
  private boolean failWhenError = true;
  /** Listeners to be notified about test generation events. */
  private GenerationListenerList listeners = new GenerationListenerList();
  /** Provides scripted values for variables. */
  private static ScriptedValueProvider scripter;
  /** Number of tests to generate when using over JUnit. */
  private int junitLength = -1;
  /** Should we try to throw original exception if model throws (remove OSMO Tester trace from the top)? */
  private boolean unwrapExceptions = true;
  /** Seed to be used for test generation. */
  private Long seed = TestUtils.getRandom().getSeed();
  /** Serialized value options for defined variables. */
  private static Map<String, ValueSet<String>> slices = new HashMap<>();
  private static boolean manual = false;

  /**
   * Adds a new model object, to be composed by OSMO to a single internal model along with other model objects.
   *
   * @param modelObject The model object (with OSMO annotations) to be added.
   */
  public void addModelObject(Object modelObject) {
    modelObjects.add(new ModelObject(modelObject));
  }

  /**
   * Adds a model object with a given prefix, allowing the same object class to be re-used with different configuration
   * where the names of transitions (test steps), guards and other elements is preceded by the given prefix.
   *
   * @param prefix      The model prefix.
   * @param modelObject The model object itself.
   */
  public void addModelObject(String prefix, Object modelObject) {
    modelObjects.add(new ModelObject(prefix, modelObject));
  }

  public Collection<ModelObject> getModelObjects() {
    return modelObjects;
  }

  public void setAlgorithm(FSMTraversalAlgorithm algorithm) {
    this.algorithm = algorithm;
  }

  public FSMTraversalAlgorithm getAlgorithm() {
    return algorithm;
  }

  /**
   * Provides the defined suite end conditions. If none are defined, one is created that requires at least
   * one test to be generated and after that ends the suite with 5% probability.
   *
   * @return The user defined suite end conditions or the default end condition.
   */
  public Collection<EndCondition> getSuiteEndConditions() {
    if (suiteEndConditions.size() == 0) {
      addSuiteEndCondition(new And(new Length(1), new Probability(0.05d)));
    }
    return suiteEndConditions;
  }

  /**
   * Provides the defined test case end conditions. If none are defined, one is created that requires at least
   * one step for a test to be generated and after that ends the test with 10% probability.
   *
   * @return The user defined suite end conditions or the default end condition.
   */
  public Collection<EndCondition> getTestCaseEndConditions() {
    if (testCaseEndConditions.size() == 0) {
      addTestEndCondition(new And(new Length(1), new Probability(0.1d)));
    }
    return testCaseEndConditions;
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
   * Adds a filter for removing some possible test steps (transitions).
   *
   * @param filter The new filter.
   */
  public void addFilter(TransitionFilter filter) {
    filters.add(filter);
    listeners.addListener(filter);
  }

  /**
   * Sets the scripter to be used for providing values, if any.
   *
   * @param scripter The new scripter.
   */
  public static void setScripter(ScriptedValueProvider scripter) {
    OSMOConfiguration.scripter = scripter;
  }

  public static ScriptedValueProvider getScripter() {
    return scripter;
  }

  /**
   * Defines if test generation should be completely stopped when the generation of a test throws an exception.
   *
   * @return True if we should stop test generation completely if the model execution throws.
   */
  public boolean shouldFailWhenError() {
    return failWhenError;
  }

  /**
   * Defines if test generation should be stopped when the model program reaches a state where no step is available.
   * If this is false, the test being generated ends there, if true all test generation stops and exception is thrown.
   *
   * @return True if should fail when model is a dead end.
   */
  public boolean shouldFailWhenNoWayForward() {
    return failWhenNoWayForward;
  }

  public Collection<TransitionFilter> getFilters() {
    return filters;
  }

  /**
   * Adds a listener to be notified about test generation progress.
   *
   * @param listener The listener to be added.
   */
  public void addListener(GenerationListener listener) {
    listeners.addListener(listener);
  }

  public GenerationListenerList getListeners() {
    return listeners;
  }

  /**
   * Initializes test generation configuration with the model to be used in test generation.
   * Includes initializing parameters for algorithms, end conditions, listeners, ..
   *
   * @param fsm The model that will be used in generation.
   */
  public void init(FSM fsm) {
    if (algorithm == null) {
      algorithm = new RandomAlgorithm();
    }
    fsm.initSearchableInputs(this);
    algorithm.init(fsm);
    for (EndCondition ec : suiteEndConditions) {
      ec.init(fsm);
    }
    listeners.init(fsm, this);
  }

  public void setFailWhenError(boolean fail) {
    failWhenError = fail;
  }

  public void setFailWhenNoWayForward(boolean fail) {
    this.failWhenNoWayForward = fail;
  }

  public int getJUnitLength() {
    return junitLength;
  }

  /**
   * Defines the number of tests to be generated when JUnit integration is used.
   *
   * @param junitLength The number of tests to be generated.
   */
  public void setJUnitLength(int junitLength) {
    this.junitLength = junitLength;
  }

  /**
   * If true, we try to throw "original" exceptions when the model program throws one.
   *
   * @return True if we try.
   */
  public boolean shouldUnwrapExceptions() {
    return unwrapExceptions || junitLength > 0;
  }

  public void setUnwrapExceptions(boolean unwrapExceptions) {
    this.unwrapExceptions = unwrapExceptions;
  }

  public long getSeed() {
    return seed;
  }

  public void setSeed(long seed) {
    //we store and set the seed here as confusion might arise otherwise if using the methods from TestUtils before
    //invoking OSMOTester.generate(). For example, to initialize model state (as in Calendar example).
    this.seed = seed;
    TestUtils.setSeed(seed);
  }

  public static void setSlices(Map<String, ValueSet<String>> values) {
    slices = values;
  }

  public static <T> ValueSet<T> getSlicesFor(String name, Deserializer<T> deserializer) {
    ValueSet<String> set = slices.get(name);
    if (set == null) {
      return null;
    }
    List<String> options = set.getOptions();
    ValueSet<T> result = new ValueSet<>();
    for (String option : options) {
      result.add(deserializer.deserialize(option));
    }
    return result;
  }

  public static void addSlice(String name, String value) {
    ValueSet<String> varSlices = slices.get(name);
    if (varSlices == null) {
      varSlices = new ValueSet<>();
      slices.put(name, varSlices);
    }
    varSlices.add(value);
  }

  public static void reset() {
    Observer.reset();
    slices = new HashMap<>();
    scripter = null;
  }

  public static boolean checkGUI(SearchableInput si) {
    if (manual == true) {
      si.enableGUI();
      return true;
    }
    return false;
  }

  public static void setManual(boolean manual) {
    OSMOConfiguration.manual = manual;
  }
}
