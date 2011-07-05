package osmo.tester.model.dataflow;

import osmo.tester.log.Logger;

import java.util.ArrayList;
import java.util.Collection;

import static osmo.tester.TestUtils.*;

/**
 * This invariant represents a set of numeric input domains.
 * Each domain is represented as a separate partition with a minimum and maximum value.
 * Input values are generated across the domains randomly and depending on the input-strategy the domain
 * is chosen in a different way each time.
 * Evaluation of values checks if the given value fits in any of the given domains.
 *
 * @author Teemu Kanstren
 */
public class ValuePartitionSet<T extends Number> {
  private static final Logger log = new Logger(ValuePartitionSet.class);
  /** The different partitions in the domain. */
  private ObjectSet<ValuePartition> partitions = new ObjectSet<ValuePartition>();
  /** The strategy for input data generation. */
  private InputStrategy strategy = InputStrategy.RANDOM;
  /** Keeps a history of all the data values created as input from this invariant. */
  protected Collection<Number> history = new ArrayList<Number>();

  /**
   * Sets the input generation strategy.
   *
   * @param strategy The new strategy.
   */
  public void setStrategy(InputStrategy strategy) {
    this.strategy = strategy;
    partitions.setStrategy(strategy);
  }

  /**
   * Adds a new data partition (domain).
   *
   * @param min Lower bound (minimum value) of the partition.
   * @param max Upper bound (maximum value) of the partition.
   */
  public void addPartition(T min, T max) {
    log.debug("Adding partition min("+min+") max("+max+")");
    if (min.doubleValue() > max.doubleValue()) {
      throw new IllegalArgumentException("Minimum value cannot be greater than maximum value.");
    }
    partitions.addOption(new ValuePartition(min, max));
  }

  /**
   * Removes the given partition. Identification is based on matching boundary values, if none are found,
   * nothing is done.
   *
   * @param min Lower bound (minimum value) of the partition.
   * @param max Upper bound (maximum value) of the partition.
   */
  public void removePartition(T min, T max) {
    log.debug("Removing partition min("+min+") max("+max+")");
    partitions.removeOption(new ValuePartition(min, max));
  }

  /**
   * Provides the next partition (domain) to generate data for, according to the chosen input-strategy.
   *
   * @return The partition to generate data from.
   */
  public ValuePartition nextPartition() {
    if (strategy != InputStrategy.OPTIMIZED_RANDOM) {
      ValuePartition partition = partitions.input();
      log.debug("Next interval "+partition);
      return partition;
    }
    return optimizedRandomPartition();
  }

  /**
   * Chooses a partition based on the OPTIMIZED_RANDOM input strategy.
   *
   * @return The chosen partition.
   */
  private ValuePartition optimizedRandomPartition() {
    log.debug("Optimized random partition choice start");
    Collection<ValuePartition> options = partitions.getAll();
    if (options.size() == 1) {
      ValuePartition partition = options.iterator().next();
      log.debug("Single partition found, returning it:"+partition);
      return partition;
    }
    int min = Integer.MAX_VALUE;
    for (ValuePartition option : options) {
      int count = coverageFor(option);
      log.debug("Coverage for "+option+":"+count);
      if (count < min) {
        min = count;
      }
    }
    log.debug("Min coverage:"+min);
    Collection<ValuePartition> currentOptions = new ArrayList<ValuePartition>();
    for (ValuePartition option : options) {
      int count = coverageFor(option);
      log.debug("Coverage for current option "+option+":"+count);
      if (count == min) {
        currentOptions.add(option);
      }
    }
    return oneOf(currentOptions);
  }

  /**
   * Calculates the coverage for the given partition. Coverage is measured in terms of how many values have
   * been generated for the given partition.
   *
   * @param partition The partition to check the coverage for.
   * @return The number of values generated so far for the given partition.
   */
  private int coverageFor(ValuePartition partition) {
    int count = 0;
    for (Number value : history) {
      if (partition.contains(value)) {
        count++;
      }
    }
    return count;
  }

  /**
   * Validates that this invariant makes sense (has partitions defined etc.).
   */
  protected void validate() {
    if (partitions.size() == 0) {
      throw new IllegalStateException("No partitions defined. Add some to use this for something.");
    }
  }

  /**
   * Generates a new double value from the next partition in line.
   *
   * @return Generated input value.
   */
  public Double nextDouble() {
    validate();
    ValuePartition i = nextPartition();
    double min = i.min().doubleValue();
    double max = i.max().doubleValue();
    double value = cDouble(min, max);
    history.add(value);
    return value;
  }

  /**
   * Generates a new integer value from the next partition in line.
   *
   * @return Generated input value.
   */
  public int nextInt() {
    validate();
    ValuePartition i = nextPartition();
    int min = i.min().intValue();
    int max = i.max().intValue();
    int value = cInt(min, max);
    history.add(value);
    return value;
  }

  /**
   * Generates a new long value from the next partition in line.
   *
   * @return Generated input value.
   */
  public long nextLong() {
    validate();
    ValuePartition i = nextPartition();
    long min = i.min().longValue();
    long max = i.max().longValue();
    long value = cLong(min, max);
    history.add(value);
    return value;
  }

  /**
   * Evaluates the given value to see if it fits into the defined set of partitions (domains).
   *
   * @param value The value to check.
   * @return True if the value fits in the defined partitions, false otherwise.
   */
  public boolean evaluate(T value) {
    Collection<ValuePartition> partitions = this.partitions.getAll();
    log.debug("Evaluating value:"+value);
    for (ValuePartition partition : partitions) {
      log.debug("Checking partition:"+partition);
      if (partition.contains(value)) {
        log.debug("Found match");
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "ValuePartitionSet{" +
            "strategy=" + strategy +
            ", partitions=" + partitions +
            '}';
  }
}
