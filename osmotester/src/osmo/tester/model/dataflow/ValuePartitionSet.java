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
public class ValuePartitionSet {
  private static final Logger log = new Logger(ValuePartitionSet.class);
  /** The different partitions in the domain. */
  private ValueSet<ValueRange> partitions = new ValueSet<ValueRange>();
  /** The strategy for input data generation. */
  private DataGenerationStrategy strategy = DataGenerationStrategy.RANDOM;

  /**
   * Sets the input generation strategy.
   *
   * @param strategy The new strategy.
   */
  public void setStrategy(DataGenerationStrategy strategy) {
    this.strategy = strategy;
    partitions.setStrategy(strategy);
  }

  /**
   * Adds a new data partition (domain).
   *
   * @param min Lower bound (minimum value) of the partition.
   * @param max Upper bound (maximum value) of the partition.
   */
  public void addPartition(double min, double max) {
    log.debug("Adding partition min("+min+") max("+max+")");
    if (min > max) {
      throw new IllegalArgumentException("Minimum value cannot be greater than maximum value.");
    }
    partitions.addOption(new ValueRange(min, max));
  }

  /**
   * Removes the given partition. Identification is based on matching boundary values, if none are found,
   * nothing is done.
   *
   * @param min Lower bound (minimum value) of the partition.
   * @param max Upper bound (maximum value) of the partition.
   */
  public void removePartition(double min, double max) {
    log.debug("Removing partition min("+min+") max("+max+")");
    partitions.removeOption(new ValueRange(min, max));
  }

  /**
   * Provides the next partition (domain) to generate data for, according to the chosen input-strategy.
   *
   * @return The partition to generate data from.
   */
  public ValueRange nextPartition() {
    if (strategy != DataGenerationStrategy.OPTIMIZED_RANDOM) {
      ValueRange partition = partitions.next();
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
  private ValueRange optimizedRandomPartition() {
    log.debug("Optimized random partition choice start");
    Collection<ValueRange> options = partitions.getAll();
    if (options.size() == 1) {
      ValueRange partition = options.iterator().next();
      log.debug("Single partition found, returning it:"+partition);
      return partition;
    }
    int min = Integer.MAX_VALUE;
    for (ValueRange option : options) {
      int count = option.getHistory().size();
      log.debug("Coverage for "+option+":"+count);
      if (count < min) {
        min = count;
      }
    }
    log.debug("Min coverage:"+min);
    Collection<ValueRange> currentOptions = new ArrayList<ValueRange>();
    for (ValueRange option : options) {
      int count = option.getHistory().size();
      log.debug("Coverage for current option "+option+":"+count);
      if (count == min) {
        currentOptions.add(option);
      }
    }
    return oneOf(currentOptions);
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
    ValueRange i = nextPartition();
//    history.add(value);
    return i.nextDouble();
  }

  /**
   * Generates a new integer value from the next partition in line.
   *
   * @return Generated input value.
   */
  public int nextInt() {
    validate();
    ValueRange i = nextPartition();
//    history.add(value);
    return i.nextInt();
  }

  /**
   * Generates a new long value from the next partition in line.
   *
   * @return Generated input value.
   */
  public long nextLong() {
    validate();
    ValueRange i = nextPartition();
//    history.add(value);
    return i.nextLong();
  }

  /**
   * Evaluates the given value to see if it fits into the defined set of partitions (domains).
   *
   * @param value The value to check.
   * @return True if the value fits in the defined partitions, false otherwise.
   */
  public boolean evaluate(double value) {
    Collection<ValueRange> partitions = this.partitions.getAll();
    log.debug("Evaluating value:"+value);
    for (ValueRange partition : partitions) {
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
