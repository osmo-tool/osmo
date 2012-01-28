package osmo.tester;

import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.GenerationListenerList;
import osmo.tester.generator.MainGenerator;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.And;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.generator.filter.TransitionFilter;
import osmo.tester.model.FSM;
import osmo.tester.model.dataflow.ScriptedValueProvider;
import osmo.tester.parser.ModelObject;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class OSMOConfiguration {
  /** The set of test model objects, given by the user. */
  private final Collection<ModelObject> modelObjects = new ArrayList<ModelObject>();
  /** When do we stop generating the overall test suite? (stopping all test generation) */
  private Collection<EndCondition> suiteEndConditions = new ArrayList<EndCondition>();
  /** When do we stop generating individual tests and start a new one? */
  private Collection<EndCondition> testCaseEndConditions = new ArrayList<EndCondition>();
  /** Set of filters to define when given transitions should not be considered for execution. */
  private Collection<TransitionFilter> filters = new ArrayList<TransitionFilter>();
  /** The algorithm to traverse the test model to generate test steps. */
  private FSMTraversalAlgorithm algorithm;
  /** Should we fail then test generation if there is not enabled transitions? Otherwise we just end the test. */
  private boolean failWhenNoWayForward = true;
  /** Should we fail test generation if an Exception is thrown? */
  private boolean failWhenError = true;
  /** Listeners to be notified about test generation events. */
  private GenerationListenerList listeners = new GenerationListenerList();
  /** Provides scripted values for variables. */
  private ScriptedValueProvider scripter;
  /** Number of tests to generate when using over JUnit. */
  private int junitLength = -1;
  private MainGenerator generator = null;

  /**
   * Adds a new model object, to be composed by OSMO to a single internal model along with other model objects.
   *
   * @param modelObject The model object (with OSMO annotations) to be added.
   */
  public void addModelObject(Object modelObject) {
    modelObjects.add(new ModelObject(modelObject));
  }

  public void addModelObject(ModelObject modelObject) {
    modelObjects.add(modelObject);
  }

  /**
   * Adds a model object with a given prefix, allowing the same object class to be re-used with different configuration
   * where the names of transitions (test steps), guards and other elements is preceded by the given prefix.
   *
   * @param prefix The model prefix.
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

  public Collection<EndCondition> getSuiteEndConditions() {
    if (suiteEndConditions.size() == 0) {
      addSuiteEndCondition(new And(new Length(1), new Probability(0.05d)));
    }
    return suiteEndConditions;
  }

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

  public void addFilter(TransitionFilter filter) {
    filters.add(filter);
    listeners.addListener(filter);
  }

  public void setScripter(ScriptedValueProvider scripter) {
    this.scripter = scripter;
  }

 public ScriptedValueProvider getScripter() {
    return scripter;
  }

  public boolean shouldFailWhenError() {
    return failWhenError;
  }

  public boolean shouldFailWhenNoWayForward() {
    return failWhenNoWayForward;
  }

  public Collection<TransitionFilter> getFilters() {
    return filters;
  }

  public void addListener(GenerationListener listener) {
    listeners.addListener(listener);
  }

 public GenerationListenerList getListeners() {
    return listeners;
  }

  public void init(FSM fsm) {
    if (algorithm == null) {
      algorithm = new RandomAlgorithm();
    }
    fsm.initSuite(scripter);
    algorithm.init(fsm);
    for (EndCondition ec : testCaseEndConditions) {
      ec.init(fsm);
    }
    for (EndCondition ec : suiteEndConditions) {
      ec.init(fsm);
    }
    listeners.init(fsm);
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

  public void setJunitLength(int junitLength) {
    this.junitLength = junitLength;
  }

  public void setGenerator(MainGenerator generator) {
    this.generator = generator;
  }
}
