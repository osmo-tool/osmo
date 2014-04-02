package osmo.tester.optimizer.reducer;

import java.util.concurrent.TimeUnit;

/**
 * Defines a configuration for performing test reduction.
 * Separate from the actual generator configuration which is also used in reduction tasks.
 * 
 * @author Teemu Kanstren
 */
public class ReducerConfig {
  /** Defines maximum time to run the overall reduction process. */
  private long totalTime = 1;
  /** Defines maximum time to run the overall reduction process. */
  private TimeUnit totalUnit = TimeUnit.HOURS;
  /** Defines maximum time to run a single iteration in a task after finding a test to reduce. */
  private long iterationTime = 5;
  /** Defines maximum time to run a single iteration in a task after finding a test to reduce. */
  private TimeUnit iterationUnit = TimeUnit.MINUTES;
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
  /** How many requirements we are looking for? If > 0 signals we are doing requirements search. Otherwise it is debug. */
  private int requirementsTarget = 0;

  public ReducerConfig(long seed) {
    this.seed = seed;
  }

  public int getRequirementsTarget() {
    return requirementsTarget;
  }

  public void setRequirementsTarget(int requirementsTarget) {
    this.requirementsTarget = requirementsTarget;
  }

  public int getParallelism() {
    return parallelism;
  }

  public void setParallelism(int parallelism) {
    this.parallelism = parallelism;
  }

  public String getPathExtension() {
    return extension;
  }

  public void setPathExtension(String extension) {
    this.extension = extension;
  }

  public long getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(TimeUnit unit, long totalTime) {
    this.totalUnit = unit;
    this.totalTime = totalTime;
  }

  public TimeUnit getTotalUnit() {
    return totalUnit;
  }

  public long getIterationTime() {
    return iterationTime;
  }

  public TimeUnit getIterationUnit() {
    return iterationUnit;
  }

  public void setIterationTime(TimeUnit unit, long iterationTime) {
    this.iterationUnit = unit;
    this.iterationTime = iterationTime;
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
}
