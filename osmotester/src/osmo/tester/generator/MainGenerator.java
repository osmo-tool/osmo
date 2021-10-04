package osmo.tester.generator;

import osmo.common.OSMOException;
import osmo.common.Logger;
import osmo.common.Randomizer;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.filter.StepFilter;
import osmo.tester.generator.listener.GenerationListenerList;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.gui.manualdrive.ManualAlgorithm;
import osmo.tester.model.CoverageMethod;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.Requirements;
import osmo.tester.parser.MainParser;
import osmo.tester.parser.ParserResult;
import osmo.tester.scenario.ScenarioFilter;
import osmo.tester.scripter.internal.TestScript;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * The main test generator class.
 * Takes as input the finite state machine model parsed by {@link osmo.tester.parser.MainParser}.
 * Runs test generation on this model using the defined algorithms, exit strategies, etc.
 *
 * @author Teemu Kanstren
 */
public class MainGenerator {
  private static final Logger log = new Logger(MainGenerator.class);
  /** The test generation configuration. */
  protected final OSMOConfiguration config;
  /** The parsed overall test model. */
  private FSM fsm;
  /** The list of listeners to be notified of new events as generation progresses. */
  private GenerationListenerList listeners;
  /** Test generation history. */
  private TestSuite suite;
  /** Keeps track of previous taken step when available. Used for coverage calculations, create state-pairs etc. */
  private TestCaseStep previousStep = null;
  /** Requirements for model objects. */
  private Requirements reqs;
  /** Keeps track of overall number of tests generated. */
  private static int testCount = 0;
  /** Set of possible pairs, key = source, value = targets. */
  private Collection<String> possiblePairs = new LinkedHashSet<>();
  /** The base seed to use for test generation. Each new test case gets a different seed based on this. */
  private final long baseSeed;
  /** To create test seeds from the baseSeed. */
  private final Randomizer seedRandomizer;
  /** The seed for the current test case being generated. */
  private Long seed = null;
  /** Forces the generator to follow the defined scenario by removing any steps from enabled list not allowed by scenario. */
  private ScenarioFilter scenarioFilter;
  private final FSMTraversalAlgorithm algorithm;
  private List<TestScript> scripts = null;
  private TestScript script = null;
  /** Tracks if we are processing an error, so if we get an error while processing an error we do not recursively do it forever. */
  private boolean inError = false;

  /**
   * @param seed   The base seed to use for randomization during generation.
   * @param suite  This is where the generated tests are stored.
   * @param config The configuration for test generation parameters.
   */
  public MainGenerator(long seed, TestSuite suite, OSMOConfiguration config) {
    this.baseSeed = seed;
    this.seedRandomizer = new Randomizer(baseSeed);
    this.seed = baseSeed;
    this.suite = suite;
    this.config = config;
    this.listeners = config.getListeners();
    this.scenarioFilter = new ScenarioFilter(config.getScenario());
    this.scripts = config.getScripts();
    suite.setKeepTests(config.isKeepTests());
    //this is used to initialize variables such as fsm
    createModelObjects();
    this.algorithm = config.cloneAlgorithm(seed, fsm);
  }

  /** Invoked to start the test generation using the configured parameters. */
  public void generate() {
    log.d("starting generation");
    config.initialize(seed, fsm);
    initSuite();
    while (!shouldEndSuite()) {
      nextTest();
    }
    log.d("Ending suite");
    endSuite();
  }

  private boolean shouldEndSuite() {
    int length = suite.currentTestNumber();
    if (scripts != null) {
      return length >= scripts.size();
    }
    //always require at least one test
    if (length < 1) return false;
    return config.getSuiteEndCondition().endSuite(suite, fsm);
  }

  /**
   * Creates all the model object instances as required. If a factory is defined, that is called.
   * If a set of classes is given, a special factory is used to create objects using reflection.
   * If no factory and no classes is given, it is assumed a set of object instances has been provided
   * by the user and these will be re-used across the generation.
   */
  private void createModelObjects() {
    int salt = suite.getCoverage().getTotalSteps();
    //create a new seed for the new test case
    seed = seedRandomizer.nextLong(); //baseSeed + salt;
    if (scripts != null) {
      script = scripts.get(suite.getAllTestCases().size());
      seed = script.getSeed();
      scenarioFilter = new ScenarioFilter(script.toScenario());
    }
    //if configuration allows empty tests, above can produce same seed and loop it.. therefor the check
    //we cannot re-parse the single instance or it will fail as it is already parsed and initialized
    if (config.getFactory() instanceof SingleInstanceModelFactory && fsm != null) return;
    //re-parse the model, which causes re-creation of the model objects and as such creates the new references
    //to the new object instances. invocationtargets for guards, steps, etc. need updating and this is needed for that.
    //also, we need to recreate the parser to avoid complaints about overlapping requirements definitions etc.
    MainParser parser = new MainParser(config);
    ParserResult result = parser.parse(seed, config, suite);
    fsm = result.getFsm();
    invokeAll(fsm.getGenerationEnablers());
    this.reqs = result.getRequirements();
    suite.initRequirements(reqs);
    fsm.setRequirements(reqs);
  }

  /**
   * Generates a new test case from the test model.
   *
   * @return The generated test case.
   */
  public TestCase nextTest() {
    inError = false;
    previousStep = null;
    createModelObjects();
    algorithm.initTest(seed);
    log.d("Starting new test generation");
    beforeTest();
    TestCase test = suite.getCurrentTest();
    while (!shouldEndTest()) {
      try {
        boolean shouldContinue = nextStep();
        if (!shouldContinue) {
          log.d("Ending test case");
          break;
        }
      } catch (RuntimeException | AssertionError e) {
        handleError(test, e);
        if (config.shouldStopTestOnError()) break;
      }
    }
    //have to put last steps here to catch all end conditions firing. have to catch again to avoid failure if so desired
    try {
      lastSteps();
    } catch (RuntimeException | AssertionError e) {
      handleError(test, e);
    }
    try {
      afterTest(test);
    } catch (RuntimeException | AssertionError e) {
      handleError(test, e);
    }
    log.d("Finished new test generation");
    return test;
  }

  private boolean shouldEndTest() {
    //always require test to have a step
    int length = getCurrentTest().getAllStepNames().size();
    if (length < 1) return false;
    if (script != null) return length >= script.getSteps().size();
    return config.getTestCaseEndCondition().endTest(suite, fsm);
  }

  private void lastSteps() {
    Collection<InvocationTarget> lastSteps = fsm.getLastSteps();
    for (InvocationTarget t : lastSteps) {
      t.invoke();
      TestCase test = getCurrentTest();
      TestCaseStep step = test.getCurrentStep();
      if (step != null) {
        //step can be null if it fails before anything else..
        storeUserCoverageValues(step);
        //store into test step the current state, do it here to allow the post methods and listeners to see step state
        suite.storeGeneralState(fsm);
      }
      listeners.lastStep(t.getMethod().getName());
    }
  }

  private void handleError(TestCase test, Throwable e) {
    inError = true;
    test.setFailed(true);
    TestCaseStep step = test.getCurrentStep();
    if (step != null) {
      step.setFailed(true);
    }
    Throwable unwrap = unwrap(e);
    String errorMsg = "Error in test generation:" + unwrap.getMessage();
    if (config.isExploring()) {
      if (config.isPrintExplorationErrors()) log.w(errorMsg, e);
    } else {
      log.e(errorMsg, e);
    }

    try {
      //TODO: tests for error listeners and onError items that throw
      listeners.testError(test, unwrap);
    } catch (Exception e1) {
      log.e("Error in invoking listeners for errors..", e);
    }
    try {
      invokeAll(fsm.getOnErrors());
    } catch (Exception e1) {
      log.e("Error in invoking @OnError..", e);
    }

    if (config.shouldStopTestOnError()) {
      suite.storeGeneralState(fsm);
      if (!config.shouldStopGenerationOnError()) return;
      //this is done here as aftertest is also invoked by the generator is we do not stop whole generation
      afterTest(test);
      afterSuite();
      inError = false;
      if (unwrap instanceof RuntimeException) {
        throw (RuntimeException) unwrap;
      }
      throw new OSMOException(errorMsg, unwrap);
    } else {
      unwrap.printStackTrace();
    }
    inError = false;
    log.d("Skipped test error due to settings (no fail when error)", e);
  }

  /**
   * Take the next step in the current test case.
   *
   * @return True if this test should end now.
   */
  private boolean nextStep() {
    List<FSMTransition> enabled = getEnabled();
    //collect all potential steps for coverage
    if (config.isTrackOptions()) addOptionsFor(suite.getCurrentTest().getCurrentStep(), enabled);
    if (enabled.size() == 0) {
      if (config.shouldFailWhenNoWayForward()) {
        throw new IllegalStateException("No test step available.");
      } else {
        log.d("No enabled steps, ending test (fail is disabled).");
        return false;
      }
    }
    FSMTransition next = algorithm.choose(suite, enabled);

    //not the best of hacks but.. manual drive ends by returning null
    if (algorithm instanceof ManualAlgorithm && next == null) return false;

    log.d("Taking step " + next.getName());
    execute(next);
    return !checkModelEndConditions();
  }

  /**
   * Executes the given transition on the given model as a test step.
   *
   * @param transition The transition to be executed.
   */
  public void execute(FSMTransition transition) {
    //we have to add this first or it will produce failures..
    TestCaseStep step = suite.addStep(transition);
    listeners.stepStarting(step);
    //store into test step the current state
    step.start();
    invokeAll(transition.getPreMethods(), "pre", transition);
    //this is the actual transition/test step being executed
    InvocationTarget target = transition.getTransition();
    try {
      target.invoke();
    } catch (Exception e) {
      if (config.shouldStopTestOnError()) {
        throw e;
      } else {
        Throwable unwrapped = unwrap(e);
        listeners.testError(getCurrentTest(), unwrapped);
        invokeAll(fsm.getOnErrors());
      }
    } finally {
      //we store the "custom" state returned by @StateName tagged methods
      //we do it here to allow any post-processing of state value for a step
      storeUserCoverageValues(step);
      //store into test step the current state, do it here to allow the post methods and listeners to see step state
      suite.storeGeneralState(fsm);
    }
    listeners.stepDone(step);
    invokeAll(transition.getPostMethods(), "post", transition);
    //set end time
    step.end();
    suite.addStepStat(step);
    previousStep = step;
    calculateAddedCoverage(step);
  }

  private void calculateAddedCoverage(TestCaseStep step) {
    ScoreCalculator sc = config.getScoreCalculator();
    if (sc == null) return;
    int added = sc.addedScoreFor(suite.getCoverage(), suite.getCurrentTest());
    step.setAddedCoverage(added);
  }

  /**
   * Stores the user defined coverage values {@link osmo.tester.annotation.CoverageValue} observed at this moment
   * into current test step.
   *
   * @param step Current test step.
   */
  private void storeUserCoverageValues(TestCaseStep step) {
    Collection<CoverageMethod> coverages = fsm.getCoverageMethods();
    for (CoverageMethod coverage : coverages) {
      String value = coverage.invoke(step);
      String name = coverage.getVariableName();
      suite.addUserCoverage(name, value);
      log.d("new coverage: " + name + "=" + value);
    }
  }

  /** Handles suite shutdown. Should be called after all tests have been generated. */
  public void endSuite() {
    afterSuite();
    log.d("Finished test suite generation");
  }

  /**
   * Invokes the given set of methods on the target test object.
   *
   * @param targets The methods to be invoked.
   */
  public void invokeAll(Collection<InvocationTarget> targets) {
    for (InvocationTarget target : targets) {
      target.invoke();
    }
  }

  /**
   * Invokes the given set of methods on the target test object.
   *
   * @param targets    The methods to be invoked.
   * @param element    Type of model element (pre or post)
   * @param transition Transition to which the invocations are related.
   */
  protected void invokeAll(Collection<InvocationTarget> targets, String element, FSMTransition transition) {
    for (InvocationTarget target : targets) {
      if (element.equals("pre")) {
        listeners.pre(transition);
      }
      if (element.equals("post")) {
        listeners.post(transition);
      }
      target.invoke();
    }
  }

  /**
   * Calls every defined end condition and if any return true, also returns true. Otherwise, false.
   *
   * @return true if current test case (not suite) generation should be stopped.
   */
  protected boolean checkModelEndConditions() {
    Collection<InvocationTarget> endConditions = fsm.getEndConditions();
    for (InvocationTarget ec : endConditions) {
      Boolean result = (Boolean) ec.invoke();
      if (result) {
        log.d("model @EndCondition signalled to stop");
        return true;
      }
    }
    return false;
  }

  /** Initializes the test suite with valid values and invokes @BeforeSuite before test generation is started. */
  public void initSuite() {
    if (suite == null) {
      log.d("No suite object defined. Creating new.");
      //use the setSuite method to also initialize the suite object missing state
      suite = new TestSuite();
    }
    suite.initRequirements(reqs);
    //re-get since suite might have initialized it if it was null
    reqs = suite.getRequirements();
    fsm.setRequirements(reqs);
    listeners.suiteStarted(suite);
    Collection<InvocationTarget> befores = fsm.getBeforeSuites();
    invokeAll(befores);
  }

  /** For @AfterSuite annotations. */
  protected void afterSuite() {
    if (suite.isEnded()) return;
    suite.setEnded(true);
    Collection<InvocationTarget> afters = fsm.getAfterSuites();
    if (!inError) invokeAll(afters);
    listeners.suiteEnded(suite);
  }

  /**
   * For @BeforeTest annotations.
   *
   * @return The new test case.
   */
  public TestCase beforeTest() {
    testCount++;
    //re-initialize end conditions before new tests to remove previous test state
    config.getTestCaseEndCondition().init(seed, fsm, config);
    //update suite
    TestCase test = suite.startTest(seed);
    listeners.testStarted(suite.getCurrentTest());
    Collection<InvocationTarget> befores = fsm.getBeforeTests();
    invokeAll(befores);
    return test;
  }

  /**
   * For @AfterTest annotations.
   *
   * @param test Test case we just ran.
   */
  public void afterTest(TestCase test) {
    if (test.isEnded()) return;
    test.setEnded(true);
    Collection<InvocationTarget> afters = fsm.getAfterTests();
    try {
      invokeAll(afters);
    } catch (RuntimeException | AssertionError e) {
      if (!inError) handleError(test, e);
    }
    //update history
    suite.endTest();
    listeners.testEnded(test);
  }

  /**
   * Goes through all {@link osmo.tester.annotation.TestStep} tagged methods in the given test model object,
   * invokes all associated {@link osmo.tester.annotation.Guard} tagged methods matching those test steps,
   * returning the set of {@link osmo.tester.annotation.TestStep} methods that have no guards returning a value
   * of {@code false}.
   *
   * @return The list of enabled {@link osmo.tester.annotation.TestStep} methods.
   */
  public List<FSMTransition> getEnabled() {
    List<FSMTransition> enabled = new ArrayList<>();
    enabled.addAll(fsm.getTransitions());
    //filter out all non-wanted transitions
    for (StepFilter filter : config.getFilters()) {
      filter.filter(enabled);
    }
    //if a scenario is defined, remove everything not part of that scenario
    scenarioFilter.filter(enabled, getCurrentTest().getAllStepNames());
    //sort the remaining ones to get deterministic test generation
    Collections.sort(enabled);
    //then check which of the remaining are allowed by their guard statements
    for (Iterator<FSMTransition> i = enabled.iterator() ; i.hasNext() ; ) {
      FSMTransition transition = i.next();
      for (InvocationTarget guard : transition.getGuards()) {
        listeners.guard(transition);
        Boolean result = (Boolean) guard.invoke();
        if (!result) {
          i.remove();
          break;
        }
      }
    }
    return enabled;
  }

  protected Throwable unwrap(Throwable e) {
    while (e instanceof OSMOException || e instanceof InvocationTargetException) {
      if (e.getCause() == null) {
        //might happen if OSMO throws OSMOException
        break;
      }
      e = e.getCause();
    }
    return e;
  }

  /** @return Number of tests generated by this generator. Not affected by suite reset. */
  public int getTestCount() {
    return testCount;
  }

  public TestSuite getSuite() {
    return suite;
  }

  public TestCase getCurrentTest() {
    return suite.getCurrentTest();
  }

  public FSM getFsm() {
    return fsm;
  }

  /**
   * Helps keep track of possible pairs of test steps encountered during test generation.
   * This can be used in reporting to list what was covered and what was not.
   *
   * @param step    The current executed step.
   * @param enabled The set of enabled steps in this state (next steps from current "step").
   */
  public void addOptionsFor(TestCaseStep step, List<FSMTransition> enabled) {
    String stepName = FSM.START_STEP_NAME;
    if (step != null) {
      stepName = step.getName();
    }
    for (FSMTransition transition : enabled) {
      possiblePairs.add(stepName + "->" + transition.getStringName());
    }
  }

  public Collection<String> getPossibleStepPairs() {
    return possiblePairs;
  }
}
