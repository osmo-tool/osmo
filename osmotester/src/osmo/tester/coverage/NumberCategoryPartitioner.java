package osmo.tester.coverage;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps given numbers to given categories for coverage calculations.
 * Numeric values are in effect processed as double precision floating points.
 * Currently this assumes to have only one category per value.
 *
 * @author Teemu Kanstren.
 */
public class NumberCategoryPartitioner {
  /** The set of defined categories. */
  private List<NumberCategory> categories = new ArrayList<>();

  /**
   * Adds a category for given parameters.
   *
   * @param min Category minimum value.
   * @param max Category maximum value.
   * @param value The actual coverage value to give if we get a hit in the numerical range.
   */
  public void addCategory(Number min, Number max, String value) {
    NumberCategory newCat = new OpenOpenNumberCategory(min, max, value);
    check(newCat);
    categories.add(newCat);
  }

  /**
   * Add a category which is open on the minimum side and closed on the maximum side.
   *
   * @param min Minimum value (inclusive).
   * @param max Maximum value (exclusive).
   * @param value The actual coverage value to give if we get a hit in the numerical range.
   */
  public void addOCCategory(Number min, Number max, String value) {
    NumberCategory newCat = new OpenClosedNumberCategory(min, max, value);
    check(newCat);
    categories.add(newCat);
  }

  /**
   * Add a category which is closed on the minimum side and open on the maximum side.
   *
   * @param min Minimum value (exclusive).
   * @param max Maximum value (inclusive).
   * @param value The actual coverage value to give if we get a hit in the numerical range.
   */
  public void addCOCategory(Number min, Number max, String value) {
    NumberCategory newCat = new ClosedOpenNumberCategory(min, max, value);
    check(newCat);
    categories.add(newCat);
  }

  /**
   * Add a category which is closed on the minimum side and closed on the maximum side.
   *
   * @param min Minimum value (exclusive).
   * @param max Maximum value (exclusive).
   * @param value The actual coverage value to give if we get a hit in the numerical range.
   */
  public void addCCCategory(Number min, Number max, String value) {
    NumberCategory newCat = new ClosedClosedNumberCategory(min, max, value);
    check(newCat);
    categories.add(newCat);
  }

  /**
   * Check if a new category is valid in itself and relation to existing ones.
   *
   * TODO: try closed categories vs open. Also if null is ever returned?
   *
   * @param newCat
   */
  private void check(NumberCategory newCat) {
    if (newCat.min.doubleValue() > newCat.max.doubleValue()) {
      throw new IllegalArgumentException("Category min cannot be larger than max:"+newCat);
    }
    for (NumberCategory oldCat : categories) {
      if (newCat.valueFor(newCat.min) != null && oldCat.valueFor(newCat.min) != null) {
        throw new IllegalArgumentException("Overlapping categories: "+newCat+" and "+oldCat);
      }
      if (newCat.valueFor(newCat.max) != null && oldCat.valueFor(newCat.max) != null) {
        throw new IllegalArgumentException("Overlapping categories: "+newCat+" and "+oldCat);
      }
    }
  }

  /**
   * Give category for specific value. If N is not included in any known category gives "OutOfBounds(N)".
   *
   * @param n The value to get category for.
   * @return Matchine category for N.
   */
  public String categoryFor(Number n) {
    for (NumberCategory category : categories) {
      String value = category.valueFor(n);
      if (value != null) return value;
    }
    return "OutOfBounds("+n+")";
  }

  /**
   * Creates a set of partition for values "0,1,N".
   *
   * @return The partition set.
   */
  public static NumberCategoryPartitioner zeroOneMany() {
    NumberCategoryPartitioner me = new NumberCategoryPartitioner();
    me.addCategory(0, 0, "zero");
    me.addCategory(1, 1, "one");
    me.addCategory(2, Integer.MAX_VALUE, "N");
    return me;
  }

  /**
   * Creates a set of partition for values "1,2,N".
   *
   * @return The partition set.
   */
  public static NumberCategoryPartitioner oneTwoMany() {
    NumberCategoryPartitioner me = new NumberCategoryPartitioner();
    me.addCategory(1, 1, "one");
    me.addCategory(2, 2, "two");
    me.addCategory(3, Integer.MAX_VALUE, "N");
    return me;
  }

  /**
   * A category class representing numbers,
   */
  private static abstract class NumberCategory {
    protected final Number min;
    protected final Number max;
    protected final String value;

    public NumberCategory(Number min, Number max, String value) {
      this.min = min;
      this.max = max;
      this.value = value;
    }

    public abstract String valueFor(Number n);
  }

  /**
   * Category where both min and max are open (inclusive).
   */
  private static class OpenOpenNumberCategory extends NumberCategory {
    public OpenOpenNumberCategory(Number min, Number max, String value) {
      super(min, max, value);
    }

    public String valueFor(Number n) {
      if (n.doubleValue() < min.doubleValue()) return null;
      if (n.doubleValue() > max.doubleValue()) return null;
      return value;
    }

    @Override
    public String toString() {
      return "cat("+min+","+max+")";
    }
  }

  /**
   * A category where minimum is open (inclusive) and maximum is closed (exclusive).
   */
  private static class OpenClosedNumberCategory extends NumberCategory {
    public OpenClosedNumberCategory(Number min, Number max, String value) {
      super(min, max, value);
    }

    public String valueFor(Number n) {
      if (n.doubleValue() < min.doubleValue()) return null;
      if (n.doubleValue() >= max.doubleValue()) return null;
      return value;
    }

    @Override
    public String toString() {
      return "cat("+min+","+max+"]";
    }
  }

  /**
   * A category where minimum is closed (exclusive) and maximum is open (inclusive).
   */
  private static class ClosedOpenNumberCategory extends NumberCategory {
    public ClosedOpenNumberCategory(Number min, Number max, String value) {
      super(min, max, value);
    }

    public String valueFor(Number n) {
      if (n.doubleValue() <= min.doubleValue()) return null;
      if (n.doubleValue() > max.doubleValue()) return null;
      return value;
    }

    @Override
    public String toString() {
      return "cat["+min+","+max+")";
    }
  }

  /**
   * A category where both minimum and maximum are closed (exclusive).
   */
  private static class ClosedClosedNumberCategory extends NumberCategory {
    public ClosedClosedNumberCategory(Number min, Number max, String value) {
      super(min, max, value);
    }

    public String valueFor(Number n) {
      if (n.doubleValue() <= min.doubleValue()) return null;
      if (n.doubleValue() >= max.doubleValue()) return null;
      return value;
    }

    @Override
    public String toString() {
      return "cat["+min+","+max+"]";
    }
  }
}
