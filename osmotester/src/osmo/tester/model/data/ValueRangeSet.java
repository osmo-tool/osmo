package osmo.tester.model.data;

import osmo.common.log.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.gui.manualdrive.ValueRangeSetGUI;

import java.util.Collection;

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
  private ValueSet<ValueRange<T>> partitions = new ValueSet<>();
  /** The strategy for selecting a partition. */
  private int strategy = RANDOM;
  /** The value used to increment value range in boundary scan. */
  private Number increment = 1;
  private T choice = null;
  public static final int RANDOM = 1;
  public static final int BALANCED = 2;
  public static final int LOOP = 3;

  public ValueRangeSet() {
  }

  @Override
  public void setSeed(long seed) {
    super.setSeed(seed);
    partitions.setSeed(seed);
    for (ValueRange range : partitions.getOptions()) {
      range.setSeed(seed);
    }
  }

  /**
   * 
   * @param strategy
   * @deprecated use the direct methods..
   */
  public void setStrategy(int strategy) {
    this.strategy = strategy;
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
    Collection<ValueRange<T>> all = partitions.getOptions();
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
      range = new ValueRange<>(Integer.class, min, max);
    } else if (min instanceof Long) {
      range = new ValueRange<>(Long.class, min, max);
    } else {
      range = new ValueRange<>(Double.class, min, max);
    }
    range.setIncrement(increment);
    range.setSeed(rand.getSeed());
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
    ValueRange<T> range = new ValueRange<>(type, min, max);
    range.setSeed(rand.getSeed());
    partitions.add(range);
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
    //we use ValueSet for partitions so we only need to handle balancing here as the valueset handles random and ordered loops
    switch (strategy) {
      case BALANCED:
        return partitions.balanced();
      case LOOP:
        return partitions.loop();
      default:
        return partitions.random();
    }
  }

  /** Validates that this range makes sense (has partitions defined etc.). */
  protected void validate() {
    if (partitions.size() == 0) {
      throw new IllegalStateException("No partitions defined. Add some to use this for something.");
    }
  }

  private void pre() {
    if (rand == null) throw new IllegalStateException("You need to set seed before using data objects");
    choice = null;
    if (gui != null) {
      choice = (T) gui.next();
      return;
    }
    OSMOConfiguration.check(this);
    validate();
  }
  
  private void post() {
    record(choice);
  }
  
  public T random() {
    pre();
    if (choice == null) {
      ValueRange vr = nextPartition();
      choice = (T) vr.random();
    }
    post();
    return choice;
  }

  public T ordered() {
    pre();
    if (choice == null) {
      ValueRange vr = nextPartition();
      choice = (T) vr.loop();
    }
    post();
    return choice;
  }

  public T balanced() {
    pre();
    if (choice == null) {
      ValueRange vr = nextPartition();
      choice = (T) vr.balanced();
    }
    post();
    return choice;
  }

  public T boundaryIn() {
    pre();
    if (choice == null) {
      ValueRange vr = nextPartition();
      choice = (T) vr.boundaryIn();
    }
    post();
    return choice;
  }

  public T boundaryOut() {
    pre();
    if (choice == null) {
      ValueRange vr = nextPartition();
      choice = (T) vr.boundaryOut();
    }
    post();
    return choice;
  }

  @Override
  public void enableGUI() {
    if (gui != null ) return;
    gui = new ValueRangeSetGUI(this);
  }

  @Override
  public String toString() {
    return "ValueRangeSet{" +
            "partitions=" + partitions +
            '}';
  }
}
