package osmo.tester.optimizer.reducer;

import java.util.concurrent.TimeUnit;

/**
 * Defines a configuration for performing test reduction.
 * Separate from the actual generator configuration which is also used in reduction tasks.
 *
 * @author Teemu Kanstren
 */
public class ReducerConfig {
  /** Defines maximum time to run a single iteration in a task after finding a test to reduce. */
  private long shorteningTime = 5;
  /** Defines maximum time to run a single iteration in a task after finding a test to reduce. */
  private TimeUnit shorteningUnit = TimeUnit.MINUTES;
  /** Defines maximum time to run a single iteration in a task after finding a test to reduce. */
  private long fuzzTime = 20;
  /** Defines maximum time to run a single iteration in a task after finding a test to reduce. */
  private TimeUnit fuzzUnit = TimeUnit.MINUTES;
  /** Defines maximum time to run a single iteration in a task after finding a test to reduce. */
  private long initialTime = 20;
  /** Defines maximum time to run a single iteration in a task after finding a test to reduce. */
  private TimeUnit initialUnit = TimeUnit.MINUTES;
  /** Number of tests to generate in an iteration for a task. */
  private int populationSize = 500;
  /** Length of a test case to generate in an iteration for a task. */
  private int length = 100;
  /** Are we running tests and should return static values for some part of reducer such as test count for reports? */
  private boolean testMode;
  /** The seed for creating more random seeds for the generators. NOT the seed for separate tasks, but seed of those seeds. */
  private final long seed;
  /** Possible extension to add to test report file name. */
  private String extension = "";
  /** Number of threads to request the thread pool to run concurrently, -> number of tasks to try to run in parallel. */
  private int parallelism = Runtime.getRuntime().availableProcessors();
  private int diversity = 10;
  /** How many requirements we are looking for? If > 0 signals we are doing requirements search. Otherwise it is d. */
  private int requirementsTarget = 0;
  private boolean printExplorationErrors = false;
  private int targetLength = -1;
  private boolean strictReduction = true;

  public ReducerConfig(long seed) {
    this.seed = seed;
  }

  public int getRequirementsTarget() {
    return requirementsTarget;
  }

  public void setRequirementsTarget(int requirementsTarget) {
    this.requirementsTarget = requirementsTarget;
  }

  public int getTargetLength() {
    return targetLength;
  }

  public void setTargetLength(int targetLength) {
    this.targetLength = targetLength;
  }

  public boolean isPrintExplorationErrors() {
    return printExplorationErrors;
  }

  public void setPrintExplorationErrors(boolean printExplorationErrors) {
    this.printExplorationErrors = printExplorationErrors;
  }

  public int getParallelism() {
    return parallelism;
  }

  public void setParallelism(int parallelism) {
    this.parallelism = parallelism;
  }

  public int getDiversity() {
    return diversity;
  }

  public void setDiversity(int diversity) {
    this.diversity = diversity;
  }

  public String getPathExtension() {
    return extension;
  }

  public void setPathExtension(String extension) {
    this.extension = extension;
  }
  public long getShorteningTime() {
    return shorteningTime;
  }

  public TimeUnit getShorteningUnit() {
    return shorteningUnit;
  }

  public void setShorteningTime(TimeUnit unit, long iterationTime) {
    this.shorteningUnit = unit;
    this.shorteningTime = iterationTime;
  }

  public long getFuzzTime() {
    return fuzzTime;
  }

  public TimeUnit getFuzzUnit() {
    return fuzzUnit;
  }

  public void setFuzzTime(TimeUnit unit, long iterationTime) {
    this.fuzzUnit = unit;
    this.fuzzTime = iterationTime;
  }

  public long getInitialTime() {
    return initialTime;
  }

  public TimeUnit getInitialUnit() {
    return initialUnit;
  }

  public void setInitialTime(TimeUnit unit, long iterationTime) {
    this.initialUnit = unit;
    this.initialTime = iterationTime;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public boolean isTestMode() {
    return testMode;
  }

  public void setTestMode(boolean testMode) {
    this.testMode = testMode;
  }

  public long getSeed() {
    return seed;
  }

  public boolean isRequirementsSearch() {
    return requirementsTarget > 0;
  }

  public void setStrictReduction(boolean strictReduction) {
    this.strictReduction = strictReduction;
  }

  public boolean isStrictReduction() {
    return strictReduction;
  }
}
