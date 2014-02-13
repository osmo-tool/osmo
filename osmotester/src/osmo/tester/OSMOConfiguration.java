package osmo.tester;

import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.generator.SingleInstanceModelFactory;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.Probability;
import osmo.tester.generator.endcondition.logical.And;
import osmo.tester.generator.filter.StepFilter;
import osmo.tester.generator.listener.GenerationListener;
import osmo.tester.generator.listener.GenerationListenerList;
import osmo.tester.model.FSM;
import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;
import osmo.tester.model.data.SearchableInput;
import osmo.tester.scenario.Scenario;
import osmo.tester.scripter.internal.TestScript;
import osmo.tester.scripter.internal.TestScripts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Defines configuration for test generation.
 *
 * @author Teemu Kanstren
 */
public class OSMOConfiguration implements ModelFactory {
  /** When do we stop generating the overall test suite? (stopping all test generation). Ignored if junitLength is set. */
  private EndCondition suiteEndCondition = new And(new Length(1), new Probability(0.05d));
  /** When do we stop generating individual tests and start a new one? */
  private EndCondition testCaseEndCondition = new And(new Length(1), new Probability(0.1d));
  /** Set of filters to define when given transitions should not be considered for execution. */
  private Collection<StepFilter> filters = new ArrayList<>();
  /** The algorithm to traverse the test model to generate test steps. */
  private FSMTraversalAlgorithm algorithm;
  /** Should we fail then test generation if there is no enabled test step? Otherwise we just end the test. */
  private boolean failWhenNoWayForward = true;
  /** Should we fail test generation if an Exception is thrown? */
  private boolean stopTestOnError = true;
  /** Listeners to be notified about test generation events. */
  private GenerationListenerList listeners = new GenerationListenerList();
  /** Number of tests to generate when using over JUnit. */
  private int junitLength = -1;
  /** Should we try to throw original exception if model throws (remove OSMO Tester trace from the top)? */
  private boolean unwrapExceptions = true;
  /** Factory for creating model objects, alternative to adding them one by one. */
  private ModelFactory factory = null;
  /** We default to this if user does not specify a factory. This is where any addModelObject() calls go. */
  private final SingleInstanceModelFactory defaultFactory = new SingleInstanceModelFactory();
  /** Is manual drive enabled? Used to enable manual GUI etc. */
  private static boolean manual = false;
  /** Model slicing scenario. */
  private Scenario scenario = null;
  /** If true, the tool will write a HTML test generation trace showing taken steps and observed values. */
  private boolean sequenceTraceRequested = false;
  /** IF true, we trace parameters for steps. */
  private boolean dataTraceRequested = false;
  /** Used to calculate coverage scores if needed. */
  private ScoreCalculator scoreCalculator;
  /** Are we running an exploration? */
  private boolean exploring = false;
  /** If true, we stop the whole generation process when an exception is thrown. */
  private boolean stopGenerationOnError = true;
  /** If true, we print out exception traces when running test model in exploration mode. */
  private boolean printExplorationErrors;
  /** Should we keep generated tests or throw them away after done? Useful for long sessions. */
  private boolean keepTests = true;
  /** If defined, only these specific tests are "generated" and executed and nothing else. */
  private List<TestScript> scripts = null;

  public OSMOConfiguration() {
  }
  
  public OSMOConfiguration(OSMOConfiguration cloneMe) {
    this.testCaseEndCondition = cloneMe.testCaseEndCondition;
    this.suiteEndCondition = cloneMe.suiteEndCondition;
    this.algorithm = cloneMe.algorithm;
    this.filters = cloneMe.filters;
    this.listeners = cloneMe.listeners;
    this.failWhenNoWayForward = cloneMe.failWhenNoWayForward;
    this.stopTestOnError = cloneMe.stopTestOnError;
    this.junitLength = cloneMe.junitLength;
    this.unwrapExceptions = cloneMe.unwrapExceptions;
    this.factory = cloneMe.factory;
    this.sequenceTraceRequested = cloneMe.sequenceTraceRequested;
    this.dataTraceRequested = cloneMe.dataTraceRequested;
    this.scoreCalculator = cloneMe.scoreCalculator;
    this.stopGenerationOnError = cloneMe.stopGenerationOnError;
    this.printExplorationErrors = cloneMe.printExplorationErrors;
    this.exploring = cloneMe.exploring;
    this.scenario = cloneMe.scenario;
  }

  public boolean isKeepTests() {
    return keepTests;
  }

  public void setKeepTests(boolean keepTests) {
    this.keepTests = keepTests;
  }

  //scenario set and get need to be synchronized as the reducer shares the config across many and all use scenario
  public synchronized void setScenario(Scenario scenario) {
    this.scenario = scenario;
  }

  public synchronized Scenario getScenario() {
    return scenario;
  }

  public void setFactory(ModelFactory factory) {
    this.factory = factory;
  }

  public boolean isExploring() {
    return exploring;
  }

  public void setExploring(boolean exploring) {
    this.exploring = exploring;
  }

  public ModelFactory getFactory() {
    if (factory == null) factory = defaultFactory;
    return factory;
  }

  public void addModelObject(String prefix, Object o) {
    defaultFactory.add(prefix, o);
  }

  public void addModelObject(Object o) {
    defaultFactory.add(o);
  }

  public void createModelObjects(TestModels addHere) {
    //force swap to default if not set
    factory = getFactory();
    factory.createModelObjects(addHere);
  }

  public void setAlgorithm(FSMTraversalAlgorithm algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * @return The defined suite end condition.
   */
  public EndCondition getSuiteEndCondition() {
    return suiteEndCondition;
  }

  /**
   * @return The test end condition.
   */
  public EndCondition getTestCaseEndCondition() {
    return testCaseEndCondition;
  }

  /**
   * Set the condition for stopping the generation of whole test suite.
   *
   * @param condition The new condition to stop overall suite generation.
   */
  public void setSuiteEndCondition(EndCondition condition) {
    suiteEndCondition = condition;
  }

  /**
   * Set the condition for stopping the generation of individual test cases.
   *
   * @param condition The new condition to stop individual test generation.
   */
  public void setTestEndCondition(EndCondition condition) {
    testCaseEndCondition = condition;
  }

  /**
   * Adds a filter for removing some possible test steps (transitions).
   *
   * @param filter The new filter.
   */
  public void addFilter(StepFilter filter) {
    filters.add(filter);
    listeners.addListener(filter);
  }

  /**
   * Defines if test generation should be completely stopped when the generation of a test throws an exception.
   *
   * @return True if we should stop test generation completely if the model execution throws.
   */
  public boolean shouldStopTestOnError() {
    return stopTestOnError;
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

  public Collection<StepFilter> getFilters() {
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
   * @param seed Test generation seed.
   * @param fsm The parsing results.
   */
  public void initialize(long seed, FSM fsm) {
    suiteEndCondition.init(seed, fsm, this);
    if (scenario != null) {
      scenario.validate(fsm);
      setTestEndCondition(scenario.createEndCondition(testCaseEndCondition));
    }
    //test end condition is initialized in generator between each test case
    listeners.init(seed, fsm, this);
  }

  public FSMTraversalAlgorithm cloneAlgorithm(long seed, FSM fsm) {
    if (algorithm == null) {
      algorithm = new RandomAlgorithm();
    }
    FSMTraversalAlgorithm clone = algorithm.cloneMe();
    clone.init(seed, fsm);
    return clone;
  }
  
  public void setStopTestOnError(boolean stop) {
    stopTestOnError = stop;
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

  public static void check(SearchableInput si) {
    if (si.isChecked()) {
      return;
    }
    si.setChecked(true);
    if (manual == true) {
      si.enableGUI();
    }
  }

  public void setManual(boolean manual) {
    this.manual = manual;
  }

  public boolean isSequenceTraceRequested() {
    return sequenceTraceRequested;
  }

  public void setSequenceTraceRequested(boolean sequenceTraceRequested) {
    this.sequenceTraceRequested = sequenceTraceRequested;
  }

  public boolean isDataTraceRequested() {
    return dataTraceRequested;
  }

  public void setDataTraceRequested(boolean dataTraceRequested) {
    this.dataTraceRequested = dataTraceRequested;
  }

  public void setScoreCalculator(ScoreCalculator scoreCalculator) {
    this.scoreCalculator = scoreCalculator;
  }

  public ScoreCalculator getScoreCalculator() {
    return scoreCalculator;
  }

  public void setStopGenerationOnError(boolean stopGenerationOnError) {
    this.stopGenerationOnError = stopGenerationOnError;
  }

  public boolean shouldStopGenerationOnError() {
    return stopGenerationOnError;
  }

  public boolean isPrintExplorationErrors() {
    return printExplorationErrors;
  }

  public void setPrintExplorationErrors(boolean printExplorationErrors) {
    this.printExplorationErrors = printExplorationErrors;
  }

  public List<TestScript> getScripts() {
    return scripts;
  }

  public void setScripts(List<TestScript> scripts) {
    this.scripts = scripts;
  }
}
