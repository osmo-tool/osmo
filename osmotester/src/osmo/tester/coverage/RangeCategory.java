package osmo.tester.coverage;

import osmo.tester.model.VariableValue;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Categorizes a set of values for an integer variable into a smaller subset of coverage values.
 * For example, all values betweel 2-N can be replaced with a value of "many".
 * Thus only one value in this range needs to be covered to cover the whole set.
 * In academic terms this is called "equivalence partitioning" or "category partitioning".
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
  /** Last observed value, set by user or taken from target. */
  private String value = null;
  /** The underlying variable from which the range value is taken, if specified. */
  private VariableValue<Integer> target = null;

  public RangeCategory() {
  }

  public RangeCategory(VariableValue<Integer> target) {
    this.target = target;
  }

  @Override
  public String value() {
    if (target != null && target.value() != null) {
      process(target.value());
    }
    return value;
  }

  public RangeCategory zeroMany() {
    addCategory(0, 0, "zero");
    addCategory(1, Integer.MAX_VALUE, "many");
    return this;
  }

  public RangeCategory zeroOneMany() {
    addCategory(0, 0, "zero");
    addCategory(1, 1, "one");
    addCategory(2, Integer.MAX_VALUE, "many");
    return this;
  }

  public RangeCategory oneTwoMany() {
    addCategory(1, 1, "zero");
    addCategory(2, 2, "one");
    addCategory(3, Integer.MAX_VALUE, "many");
    return this;
  }

  /**
   * Adds a new range category with the given specs.
   *
   * @param min  Range minimum.
   * @param max  Range maximum.
   * @param name chaining reference.
   */
  public RangeCategory addCategory(int min, int max, String name) {
    if (name == null) {
      throw new NullPointerException("Range name cannot be null.");
    }
    IntegerRange range = new IntegerRange(min, max, name);
    ranges.add(range);
    return this;
  }

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
