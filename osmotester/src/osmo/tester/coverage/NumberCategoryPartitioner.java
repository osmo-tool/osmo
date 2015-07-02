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
  private List<NumberCategory> categories = new ArrayList<>();

  public void addCategory(Number min, Number max, String value) {
    NumberCategory newCat = new OpenOpenNumberCategory(min, max, value);
    check(newCat);
    categories.add(newCat);
  }

  public void addOCCategory(Number min, Number max, String value) {
    NumberCategory newCat = new OpenClosedNumberCategory(min, max, value);
    check(newCat);
    categories.add(newCat);
  }

  public void addCOCategory(Number min, Number max, String value) {
    NumberCategory newCat = new ClosedOpenNumberCategory(min, max, value);
    check(newCat);
    categories.add(newCat);
  }

  public void addCCCategory(Number min, Number max, String value) {
    NumberCategory newCat = new ClosedClosedNumberCategory(min, max, value);
    check(newCat);
    categories.add(newCat);
  }

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

  public String categoryFor(Number n) {
    for (NumberCategory category : categories) {
      String value = category.valueFor(n);
      if (value != null) return value;
    }
    return "OutOfBounds("+n+")";
  }

  public static NumberCategoryPartitioner zeroOneMany() {
    NumberCategoryPartitioner me = new NumberCategoryPartitioner();
    me.addCategory(0, 0, "zero");
    me.addCategory(1, 1, "one");
    me.addCategory(2, Integer.MAX_VALUE, "N");
    return me;
  }

  public static NumberCategoryPartitioner oneTwoMany() {
    NumberCategoryPartitioner me = new NumberCategoryPartitioner();
    me.addCategory(1, 1, "one");
    me.addCategory(2, 2, "two");
    me.addCategory(3, Integer.MAX_VALUE, "N");
    return me;
  }

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
