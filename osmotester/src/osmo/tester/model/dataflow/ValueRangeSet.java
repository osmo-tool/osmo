package osmo.tester.model.dataflow;

import osmo.common.log.Logger;

import java.util.ArrayList;
import java.util.Collection;

import static osmo.common.TestUtils.oneOf;

/**
 * Represents a set of numeric input domains.
 * Each domain is represented as a separate partition with a minimum and maximum value.
 * Input values are generated across the domains randomly and depending on the input-strategy the domain
 * is chosen in a different way each time.
 * Evaluation of values checks if the given value fits in any of the given domains.
 *
 * @author Teemu Kanstren
 */
public class ValueRangeSet<T extends Number> implements Input<T>, Output<T>{
  private static final Logger log = new Logger(ValueRangeSet.class);
  /** The different partitions in the domain. */
  private ValueSet<ValueRange> partitions = new ValueSet<ValueRange>();
  /** The strategy for input data generation. */
  private DataGenerationStrategy strategy = DataGenerationStrategy.RANDOM;

  /**
   * Sets the input generation strategy.
   *
   * @param strategy The new strategy.
   */
  @Override
  public void setStrategy(DataGenerationStrategy strategy) {
    this.strategy = strategy;
    partitions.setStrategy(strategy);
  }

  /**
   * Adds a new data partition (domain). Type is inferred from the type of the "min" parameter.
   * See {@link ValueRange} for more information on the types.
   *
   * @param min Lower bound (minimum value) of the partition.
   * @param max Upper bound (maximum value) of the partition.
   */
  public void addPartition(Number min, Number max) {
    log.debug("Adding partition min("+min+") max("+max+")");
    validateRange(min, max);
    if (min instanceof Integer) {
      partitions.add(new ValueRange<Integer>(Integer.class, min, max));
    } else if (min instanceof Long) {
      partitions.add(new ValueRange<Long>(Long.class, min, max));
    } else {
      partitions.add(new ValueRange<Double>(Double.class, min, max));
    }
  }

  /**
   * Adds a new data partition (domain).
   * The type to be generated is explicitly given.
   * See {@link ValueRange} for more information on the types.
   *
   * @param type The type of numbers in value ranges.
   * @param min Lower bound (minimum value) of the partition.
   * @param max Upper bound (maximum value) of the partition.
   */
  public void addPartition(Class<T> type, Number min, Number max) {
    log.debug("Adding partition min("+min+") max("+max+")");
    validateRange(min, max);
    partitions.add(new ValueRange<T>(type, min, max));
  }

  private void validateRange(Number min, Number max) {
    if (min.doubleValue() > max.doubleValue()) {
      throw new IllegalArgumentException("Minimum value cannot be greater than maximum value.");
    }
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
    partitions.remove(new ValueRange(min, max));
  }

  /**
   * Provides the next partition (domain) to generate data for, according to the chosen input-strategy.
   *
   * @return The partition to generate data from.
   */
  public ValueRange nextPartition() {
    //we use ValueSet for partitions so we only need to handle optimized random here as the valueset handles random and ordered loops
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
    //first we find the minimum coverage in the set
    int min = Integer.MAX_VALUE;
    for (ValueRange option : options) {
      int count = option.getHistory().size();
      log.debug("Coverage for "+option+":"+count);
      if (count < min) {
        min = count;
      }
    }
    log.debug("Min coverage:"+min);
    //then we find all that have coverage equal to smallest
    Collection<ValueRange> currentOptions = new ArrayList<ValueRange>();
    for (ValueRange option : options) {
      int count = option.getHistory().size();
      log.debug("Coverage for current option "+option+":"+count);
      if (count == min) {
        currentOptions.add(option);
      }
    }
    //finally we pick one from the set of smallest coverage
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

  @Override
  public T next() {
    validate();
    ValueRange i = nextPartition();
//    history.add(value);
    if (i.getType() == DataType.INT) {
      return (T)new Integer(i.nextInt());
    } else if (i.getType() == DataType.LONG) {
      return (T)new Long(i.nextLong());
    } else {
      return (T)new Double(i.nextDouble());
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
  public boolean evaluate(T value) {
    Collection<ValueRange> partitions = this.partitions.getAll();
    log.debug("Evaluating value:"+value);
    for (ValueRange partition : partitions) {
      log.debug("Checking partition:"+partition);
      if (partition.evaluate(value)) {
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
