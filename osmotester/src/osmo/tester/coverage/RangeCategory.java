package osmo.tester.coverage;

import osmo.tester.model.VariableValue;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Categorizes a set of values for an integer variable into a smaller subset of coverage values.
 * For example, all values between 2-N can be replaced with a value of "many".
 * Thus only one value in this range needs to be covered to cover the whole set.
 * In academic terms this is typically called "equivalence partitioning" or "category partitioning".
 * The ranges/categories can be overlapping in which case many values may be generated for a single value.
 * For example, 1-1="one", 1-5="some", observing 1 will produce "one" and "some" coverage values.
 * Notice that if an input value is observed that is not part of any defined category, the value is simply
 * discarded.
 *
 * @author Teemu Kanstren
 */
public class RangeCategory implements VariableValue {
  /** The set of ranges or categories for this coverage variable. */
  private Collection<IntegerRange> ranges = new ArrayList<>();
  /** Last observed value, set by user (manually coded) or taken from target (automatic pickup from reference). */
  private String value = null;
  /** The underlying variable from which the range value is taken, if specified. */
  private VariableValue<Integer> target = null;

  public RangeCategory() {
  }

  public RangeCategory(VariableValue<Integer> target) {
    this.target = target;
  }

  /**
   * Turns the value taken from the target, or as set by user code, into one of the available ranges.
   * 
   * @return The matching range, or null if none.
   */
  @Override
  public String value() {
    if (target != null && target.value() != null) {
      process(target.value());
    }
    return value;
  }

  /**
   * Creates a set of ranges where value 0 is "zero" partition and anything bigger is "many" partition.
   * 
   * @return The predefined range.
   */
  public RangeCategory zeroMany() {
    addCategory(0, 0, "zero");
    addCategory(1, Integer.MAX_VALUE, "many");
    return this;
  }

  /**
   * Creates a set of ranges where value 0 is "zero" partition, value 1 is "one" partition,
   * and anything bigger is "many" partition.
   *
   * @return The predefined range.
   */
  public RangeCategory zeroOneMany() {
    addCategory(0, 0, "zero");
    addCategory(1, 1, "one");
    addCategory(2, Integer.MAX_VALUE, "many");
    return this;
  }

  /**
   * Creates a set of ranges where value 1 is "one" partition, value 2 is "two" partition,
   * and anything bigger is "many" partition.
   *
   * @return The predefined range.
   */
  public RangeCategory oneTwoMany() {
    addCategory(1, 1, "one");
    addCategory(2, 2, "two");
    addCategory(3, Integer.MAX_VALUE, "many");
    return this;
  }

  /**
   * Adds a new range category with the given specs.
   *
   * @param min  Range minimum.
   * @param max  Range maximum.
   * @param name Defines what to call the new value if it hits inside the range.
   */
  public RangeCategory addCategory(int min, int max, String name) {
    if (name == null) {
      throw new NullPointerException("Range name cannot be null.");
    }
    IntegerRange range = new IntegerRange(min, max, name);
    ranges.add(range);
    return this;
  }

  /**
   * Processes a given value and sets the value attribute to the matching range name if found.
   * If no matching range is found, the value is set to null.
   * 
   * @param value The value to process.
   * @return Self reference (for chained calls).
   */
  public RangeCategory process(int value) {
    boolean found = false;
    for (IntegerRange range : ranges) {
      if (value >= range.min && value <= range.max) {
        this.value = range.name;
        found = true;
      }
    }
    if (!found) this.value = null;
    return this;
  }

  /**
   * Access to the defined range categories.
   *
   * @return The defined range categories.
   */
  public Collection<IntegerRange> getRanges() {
    return ranges;
  }
}
