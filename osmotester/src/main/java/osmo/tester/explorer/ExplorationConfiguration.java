package osmo.tester.explorer;

import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.algorithm.RandomAlgorithm;
import osmo.tester.model.FSM;
import osmo.tester.model.ModelFactory;
import osmo.tester.scenario.Scenario;

/**
 * Defines a configuration for OSMO Explorer.
 * Defines the maximum and minimum lengths for tests and test suites, as well as a threshold when to stop if no gain
 * is observed in coverage.
 *
 * @author Teemu Kanstren
 */
public class ExplorationConfiguration extends ScoreConfiguration {
  /** The factory used to create the model objects every time a new path needs to be explored. */
  protected ModelFactory factory;
  /** The depth of exploration, that is how far along every possible path does it explore on the current test path. */
  protected int depth;
  /** Minimum length for each generated test case, exploration will not stop before this. */
  protected int minTestLength = 10;
  /** Maximum length for each generated test case, exploration will stop for this test here if not before. */
  protected int maxTestLength = 50;
  /** Minimum length for generated test suite, exploration will not stop before this. */
  protected int minSuiteLength = 10;
  /** Maximum length for generated test suite, exploration will stop here if not before. */
  protected int maxSuiteLength = 20;
  /** Minimum added score to stop generation of current test. Minimum and Maximum test length have precedence over this. */
  protected int minTestScore = 0;
  /** Minimum added score to stop suite generation. Minimum and Maximum suite length have precedence over this. */
  protected int minSuiteScore = 0;
  /** The threshold to stop test generation if no gain is observed over test. */
  protected int testPlateauThreshold = 1;
  /** How many steps with minimum score together before we consider to have hit a plateau? */
  protected int testPlateauLength = 1;
  /** The threshold to stop test generation if no gain is observed over suite. */
  protected int suitePlateauThreshold = 1;
  /** The probability to stop after minimum values are reached. Defaults to 0.2 (1=immediate stop,0=no stop). */
  protected double fallbackProbability = 0.2;
  /** The end condition created from the configuration defined in this class. */
  protected ExplorationEndCondition endCondition = null;
  /** Max number of seconds to run, after which the exploration is stopped if not before. 0 or less means never. */
  protected int timeout = 30*60;
  /** Randomization seed. */
  private final long seed;
  /** Fallback algorithm for step selection. */
  private FSMTraversalAlgorithm fallback;
  /** If true, we print all uncovered options (if known). Can lead to huge prints if state/step-space is large. */
  private boolean printAll = false;
  /** Number of parallel threads to use for exploration, defaults to the number of processors on host. */
  private int parallelism = Runtime.getRuntime().availableProcessors();
  /** Possible scenario if any to use before starting exploration. */
  private Scenario scenario;
  /** Passed to underlying generator, stops generation when no path forward is seen. Otherwise, new test is started. */
  private boolean failWhenNoWayForward;
  private boolean stopGenerationOnError;

  public ExplorationConfiguration(ModelFactory factory, int depth, long seed) {
    this.factory = factory;
    this.depth = depth;
    this.seed = seed;
    this.lengthWeight = 0;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public boolean isPrintAll() {
    return printAll;
  }

  public void setPrintAll(boolean printAll) {
    this.printAll = printAll;
  }

  public int getParallelism() {
    return parallelism;
  }

  public void setParallelism(int parallelism) {
    this.parallelism = parallelism;
  }

  public long getSeed() {
    return seed;
  }

  public void setFactory(ModelFactory factory) {
    this.factory = factory;
  }

  public ModelFactory getModelFactory() {
    return factory;
  }

  public int getDepth() {
    return depth;
  }

  public int getTimeout() {
    return timeout;
  }

  /**
   * Set the timeout after which exploration should always stop.
   * 
   * @param timeout Timeout in seconds.
   */
  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  /**
   * Sets the end conditions and model factory.
   * 
   * @param config Where they should be set.
   */
  public void fillOSMOConfiguration(OSMOConfiguration config) {
    this.endCondition = new ExplorationEndCondition(this);
    config.setScoreCalculator(new ScoreCalculator(this));
    config.setSuiteEndCondition(endCondition);
    config.setTestEndCondition(endCondition);
    config.setFactory(factory);
    config.setScenario(scenario);
    config.setFailWhenNoWayForward(failWhenNoWayForward);
    config.setStopGenerationOnError(stopGenerationOnError);
  }

  public int getMinTestLength() {
    return minTestLength;
  }

  public void setMinTestLength(int minTestLength) {
    this.minTestLength = minTestLength;
  }

  public int getMaxTestLength() {
    return maxTestLength;
  }

  public void setMaxTestLength(int maxTestLength) {
    this.maxTestLength = maxTestLength;
  }

  public int getMinSuiteLength() {
    return minSuiteLength;
  }

  public void setMinSuiteLength(int minSuiteLength) {
    this.minSuiteLength = minSuiteLength;
  }

  public int getMaxSuiteLength() {
    return maxSuiteLength;
  }

  public void setMaxSuiteLength(int maxSuiteLength) {
    this.maxSuiteLength = maxSuiteLength;
  }

  public int getMinTestScore() {
    return minTestScore;
  }

  public void setMinTestScore(int minTestScore) {
    this.minTestScore = minTestScore;
  }

  public int getMinSuiteScore() {
    return minSuiteScore;
  }

  public void setMinSuiteScore(int minSuiteScore) {
    this.minSuiteScore = minSuiteScore;
  }

  public ExplorationEndCondition getExplorationEndCondition() {
    return endCondition;
  }

  public int getTestPlateauThreshold() {
    return testPlateauThreshold;
  }

  public void setTestPlateauThreshold(int testPlateauThreshold) {
    this.testPlateauThreshold = testPlateauThreshold;
  }

  public int getTestPlateauLength() {
    return testPlateauLength;
  }

  public void setTestPlateauLength(int testPlateauLength) {
    this.testPlateauLength = testPlateauLength;
  }

  public int getSuitePlateauThreshold() {
    return suitePlateauThreshold;
  }

  public void setSuitePlateauThreshold(int suitePlateauThreshold) {
    this.suitePlateauThreshold = suitePlateauThreshold;
  }

  public FSMTraversalAlgorithm getFallback(long seed, FSM fsm) {
    if (fallback == null) {
      setFallback(new RandomAlgorithm(), seed, fsm);
    }
    FSMTraversalAlgorithm clone = fallback.cloneMe();
    clone.init(seed, fsm);
    clone.initTest(seed);
    return clone;
  }

  public void setFallback(FSMTraversalAlgorithm fallback, long seed, FSM fsm) {
    this.fallback = fallback;
    fallback.init(seed, fsm);
    fallback.initTest(seed);
  }

  public double getFallbackProbability() {
    return fallbackProbability;
  }

  public void setFallbackProbability(double fallbackProbability) {
    this.fallbackProbability = fallbackProbability;
  }

  /**
   * Checks this configuration for validity. A valid configuration is one where
   * -both the suite and test have at the minimum one of minimum length or minimum score defined.
   * -the minimum length is equal to, or less than maximum length
   * -the minimum length, maximum length, and minimum score are higher than 0
   * (value of zero for any means it is not defined)
   * -also delegates to the {@link osmo.tester.coverage.ScoreConfiguration} parent validate call.
   * 
   * @param fsm the model to check against.
   */
  public void validate(FSM fsm) {
    super.validate(fsm);
    String errors = "";
    boolean suiteLength = minSuiteLength > 0;
    boolean suiteScore = minSuiteScore > 0;
    boolean testLength = minTestLength > 0;
    boolean testScore = minTestScore > 0;
    String ln = System.lineSeparator();
    if (!suiteLength && !suiteScore) {
      errors += "Exploration requires defining either minimum suite length or minimum suite score." + ln;
    }
    if (!testLength && !testScore) {
      errors += "Exploration requires defining either minimum test length or minimum test score." + ln;
    }
    if (maxSuiteLength > 0 && minSuiteLength > 0) {
      if (maxSuiteLength < minSuiteLength) {
        errors += "Suite minimum length must be less than maximum length." + ln;
      }
    }
    if (maxTestLength > 0 && minTestLength > 0) {
      if (maxTestLength < minTestLength) {
        errors += "Test case minimum length must be less than maximum length." + ln;
      }
    }
    double probability = fallbackProbability;
    if (probability < 0 || probability > 1) {
      errors += "Fallback probability must be between 0 and 1. Was " + probability + ".";
    }
    if (errors.length() > 0) {
      errors = "Invalid exploration configuration:" + ln + errors;
      throw new IllegalArgumentException(errors);
    }
  }

  @Override
  public String toString() {
    return "ExplorationConfiguration{\n" +
            "factory=" + factory.getClass() +
            ",\n depth=" + depth +
            ",\n minTestLength=" + minTestLength +
            ",\n maxTestLength=" + maxTestLength +
            ",\n minSuiteLength=" + minSuiteLength +
            ",\n maxSuiteLength=" + maxSuiteLength +
            ",\n minTestScore=" + minTestScore +
            ",\n minSuiteScore=" + minSuiteScore +
            ",\n testPlateauThreshold=" + testPlateauThreshold +
            ",\n suitePlateauThreshold=" + suitePlateauThreshold +
            ",\n fallbackProbability=" + fallbackProbability +
            ",\n endCondition=" + endCondition +
            ",\n timeout=" + timeout +
            "}\n " + super.toString();
  }

  public void setScenario(Scenario scenario) {
    this.scenario = scenario;
  }

  public Scenario getScenario() {
    return scenario;
  }

  public void setFailWhenNoWayForward(boolean failWhenNoWayForward) {
    this.failWhenNoWayForward = failWhenNoWayForward;
  }

  public boolean isFailWhenNoWayForward() {
    return failWhenNoWayForward;
  }

  public void setStopGenerationOnError(boolean stopGenerationOnError) {
    this.stopGenerationOnError = stopGenerationOnError;
  }

  public boolean isStopGenerationOnError() {
    return stopGenerationOnError;
  }
}
