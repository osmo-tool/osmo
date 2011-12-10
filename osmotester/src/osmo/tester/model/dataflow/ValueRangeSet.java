package osmo.tester.model.dataflow;

import osmo.common.log.Logger;
import osmo.tester.gui.manualdrive.ValueRangeSetGUI;

import java.util.ArrayList;
import java.util.Collection;

import static osmo.common.TestUtils.oneOf;

/**
 * Represents a set of numeric input domains.
 * Each domain is represented as a separate partition with a minimum and maximum value.
 * Input values are generated across the domains randomly and depending on the input-strategy the domain
 * is chosen in a different way each time.
 * Evaluation of values checks if the given value fits in any of the given domains.
 * NOTE: Use Double as datatype for floating point numbers, not Float. Float is not supported in this class at this time.
 *
 * @author Teemu Kanstren
 */
public class ValueRangeSet<T extends Number> extends SearchableInput<T> {
  private static final Logger log = new Logger(ValueRangeSet.class);
  /** The different partitions in the domain. */
  private ValueSet<ValueRange> partitions = new ValueSet<ValueRange>();
  /** The strategy for selecting a partition. */
  private DataGenerationStrategy strategy = DataGenerationStrategy.RANDOM;
  /** The strategy for input data generation from the partitions. */
  private DataGenerationStrategy partitionStrategy = DataGenerationStrategy.RANDOM;
  /** The value used to increment value range in boundary scan. */
  private Number increment = 1;

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
   * Set the data generation strategy for the composing partitions.
   *
   * @param strategy The new strategy.
   */
  public void setPartitionStrategy(DataGenerationStrategy strategy) {
    this.partitionStrategy = strategy;
    Collection<ValueRange> all = partitions.getOptions();
    for (ValueRange range : all) {
      range.setStrategy(partitionStrategy);
    }
  }

  public ValueRange getPartition(int i) {
    return partitions.getOptions().get(i);
  }

  /**
   * Set the increment value for all partitions.
   *
   * @param increment Increment for value range partitions.
   */
  public void setIncrement(Number increment) {
    Collection<ValueRange> all = partitions.getOptions();
    for (ValueRange range : all) {
      range.setIncrement(increment);
    }
  }

  /**
   * Adds a new data partition (domain). Type is inferred from the type of the "min" parameter.
   * See {@link ValueRange} for more information on the types.
   *
   * @param min Lower bound (minimum value) of the partition.
   * @param max Upper bound (maximum value) of the partition.
   */
  public void addPartition(Number min, Number max) {
    log.debug("Adding partition min(" + min + ") max(" + max + ")");
    validateRange(min, max);
    ValueRange range = null;
    if (min instanceof Integer) {
      range = new ValueRange<Integer>(Integer.class, min, max);
    } else if (min instanceof Long) {
      range = new ValueRange<Long>(Long.class, min, max);
    } else {
      range = new ValueRange<Double>(Double.class, min, max);
    }
    range.setStrategy(partitionStrategy);
    range.setIncrement(increment);
    partitions.add(range);
  }

  /**
   * Adds a new data partition (domain).
   * The type to be generated is explicitly given.
   * See {@link ValueRange} for more information on the types.
   *
   * @param type The type of numbers in value ranges.
   * @param min  Lower bound (minimum value) of the partition.
   * @param max  Upper bound (maximum value) of the partition.
   */
  public void addPartition(Class<T> type, Number min, Number max) {
    log.debug("Adding partition min(" + min + ") max(" + max + ")");
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
    log.debug("Removing partition min(" + min + ") max(" + max + ")");
    partitions.remove(new ValueRange(min, max));
  }

  /**
   * Provides the next partition (domain) to generate data for, according to the chosen input-strategy.
   *
   * @return The partition to generate data from.
   */
  public ValueRange nextPartition() {
    //we use ValueSet for partitions so we only need to handle optimized random here as the valueset handles random and ordered loops
    if (strategy != DataGenerationStrategy.LESS_RANDOM) {
      ValueRange partition = partitions.next();
      log.debug("Next interval " + partition);
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
    Collection<ValueRange> options = partitions.getOptions();
    if (options.size() == 1) {
      ValueRange partition = options.iterator().next();
      log.debug("Single partition found, returning it:" + partition);
      return partition;
    }
    //first we find the minimum coverage in the set
    int min = Integer.MAX_VALUE;
    for (ValueRange option : options) {
      int count = option.getHistory().size();
      log.debug("Coverage for " + option + ":" + count);
      if (count < min) {
        min = count;
      }
    }
    log.debug("Min coverage:" + min);
    //then we find all that have coverage equal to smallest
    Collection<ValueRange> currentOptions = new ArrayList<ValueRange>();
    for (ValueRange option : options) {
      int count = option.getHistory().size();
      log.debug("Coverage for current option " + option + ":" + count);
      if (count == min) {
        currentOptions.add(option);
      }
    }
    //finally we pick one from the set of smallest coverage
    return oneOf(currentOptions);
  }

  /** Validates that this range makes sense (has partitions defined etc.). */
  protected void validate() {
    if (partitions.size() == 0) {
      throw new IllegalStateException("No partitions defined. Add some to use this for something.");
    }
  }

  @Override
  public T next() {
    validate();
    if (gui != null) {
      return (T)gui.next();
    }
    T next;
    if (strategy == DataGenerationStrategy.SCRIPTED) {
      return scriptedNext(scriptNextSerialized());
    }
    ValueRange i = nextPartition();
//    history.add(value);
    if (i.getType() == DataType.INT) {
      next = (T) new Integer(i.nextInt());
    } else if (i.getType() == DataType.LONG) {
      next = (T) new Long(i.nextLong());
    } else {
      next = (T) new Double(i.nextDouble());
    }
    observe(next);
    return next;
  }

  private T scriptedNext(String serialized) {
    if (!evaluateSerialized(serialized)) {
      throw new IllegalArgumentException("Requested invalid scripted value for variable '"+getName()+"': "+serialized);
    }
    DataType type = partitions.getOptions().iterator().next().getType();
    Number value = null;
    switch (type) {
      case INT:
        value = Integer.parseInt(serialized);
        break;
      case LONG:
        value = Long.parseLong(serialized);
        break;
      case DOUBLE:
        value = Double.parseDouble(serialized);
        break;
      default:
        throw new IllegalArgumentException("Enum type:" + type + " unsupported.");
    }
    return (T)value;
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
  @Override
  public boolean evaluate(Number value) {
    Collection<ValueRange> partitions = this.partitions.getOptions();
    log.debug("Evaluating value:" + value);
    for (ValueRange partition : partitions) {
      log.debug("Checking partition:" + partition);
      if (partition.evaluate(value)) {
        log.debug("Found match");
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean evaluateSerialized(String item) {
    double value = 0;
    try {
      value = Double.parseDouble(item);
    } catch (NumberFormatException e) {
      return false;
    }
    return evaluate(value);
  }

  @Override
  public void enableGUI() {
    gui = new ValueRangeSetGUI(this);
  }

  @Override
  public String toString() {
    return "ValuePartitionSet{" +
            "strategy=" + strategy +
            ", partitions=" + partitions +
            '}';
  }
}
