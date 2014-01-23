package osmo.tester.optimizer.reducer;

import java.util.concurrent.TimeUnit;

/**
 * @author Teemu Kanstren
 */
public class ReducerConfig {
  private long totalTime = 1;
  private TimeUnit totalUnit = TimeUnit.HOURS;
  private long iterationTime = 5;
  private TimeUnit iterationUnit = TimeUnit.MINUTES;
  private int populationSize = 500;
  private int length = 100;
  private boolean testMode;
  /** The seed for creating more random seeds for the generators. NOT the seed for separate tasks. */
  private final long seed;
  private String extension = "";
  private int parallelism = Runtime.getRuntime().availableProcessors();

  public ReducerConfig(long seed) {
    this.seed = seed;
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
}
