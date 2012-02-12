package osmo.tester.model.dataflow;

import osmo.common.log.Logger;
import osmo.tester.gui.manualdrive.ValueRangeGUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static osmo.common.TestUtils.*;

/**
 * Defines a value range with a minimum and maximum values. Generates input from this range, including min and max.
 * Evaluates given values if they fit in the range.
 * Support subclasses of {@link Number}, specifically {@link Integer}, {@link Long}, and {@link Double}.
 * Notice that due to limitations of the Java generics type system, you have to either provide the type
 * as an explicit argument to the constructor or otherwise the type to be generated will be based on the
 * type of object passed as the "min" value of the range.
 * <p/>
 * To get integers do either
 * new ValueRange<Integer>(1,2); or
 * new ValueRange<Integer>(Integer.class, 1, 2);
 * <p/>
 * to get long values do either
 * new ValueRange<Long>(1l,2l); or
 * new ValueRange<Long>(Long.class, 1, 2);
 * <p/>
 * to get double values do either
 * new ValueRange<Double>(1d,2d); or
 * new ValueRange<Double>(Double.class, 1, 2);
 * <p/>
 * Omitting the type information will result in generation of integers.
 * It is also possible to use any of the specific functions such as nextInt(), nextLong() and nextDouble()
 * regardless of configured type.
 *
 * @author Teemu Kanstren
 * @see Input
 * @see Output
 */
public class ValueRange<T extends Number> extends SearchableInput<T> {
  private static final Logger log = new Logger(ValueRange.class);
  /** Minimum value for this value range. */
  private Number min;
  /** Maximum value for this value range. */
  private Number max;
  /** Amount to increment with if using ordered loop data generation strategy. */
  private Number increment = 1;
  /** Keeps a history of all the data values created as input from this value range. */
  protected List<Number> history = new ArrayList<>();
  /** History of generated values in case an optimized data generation strategy is used. */
  protected List<Number> optimizerHistory = new ArrayList<>();
  /** The strategy for data generation. */
  private DataGenerationStrategy algorithm = DataGenerationStrategy.RANDOM;
  /** The actual type of data to be generated. */
  private final DataType type;
  /** Handles boundary scan data generation strategy. */
  private final Boundary boundary;

  /**
   * Constructor that takes an explicit type argument for generation.
   *
   * @param type The type of data to be generated.
   * @param min  Minimum value of the range.
   * @param max  Maximum value of the range.
   */
  public ValueRange(Class<T> type, Number min, Number max) {
    this.min = min;
    this.max = max;

    if (type.equals(Integer.class)) {
      this.type = DataType.INT;
    } else if (type.equals(Long.class)) {
      this.type = DataType.LONG;
    } else {
      this.type = DataType.DOUBLE;
    }
    boundary = new Boundary(this.type, min, max);
    allSupported = true;
  }

  /**
   * Constructor that tries to infer generated data type from the type of the "min" parameter.
   *
   * @param min Minimum value of the range.
   * @param max Maximum value of the range.
   */
  public ValueRange(Number min, Number max) {
    this.min = min;
    this.max = max;

    if (min instanceof Integer) {
      this.type = DataType.INT;
    } else if (min instanceof Long) {
      this.type = DataType.LONG;
    } else {
      this.type = DataType.DOUBLE;
    }
    boundary = new Boundary(this.type, min, max);
    allSupported = true;
  }

  public DataType getType() {
    return type;
  }

  /** @param increment The value to increment with for ordered loops. */
  public void setIncrement(Number increment) {
    this.increment = increment;
    boundary.setIncrement(increment);
  }

  public void setCount(int count) {
    boundary.setCount(count);
  }

  public Number min() {
    return min;
  }

  public void setMin(Number min) {
    this.min = min;
  }

  public Number max() {
    return max;
  }

  public void setMax(Number max) {
    this.max = max;
  }

  public Collection<Number> getHistory() {
    return history;
  }

  @Override
  public void setStrategy(DataGenerationStrategy algorithm) {
    this.algorithm = algorithm;
  }

  @Override
  public T next() {
    if (gui != null) {
      return (T) gui.next();
    }
    return (T) next(type);
  }

  /**
   * Generates a new numeric value of given type in this value range.
   *
   * @param type The datatype to be generated.
   * @return Generated input value.
   */
  public Number next(DataType type) {
    Number value = 0;
    switch (algorithm) {
      case ORDERED_LOOP:
        value = nextOrderedLoop(type);
        break;
      case BALANCING:
        value = nextOptimizedRandom(type);
        break;
      case BOUNDARY_SCAN:
        value = nextBoundaryScan();
        break;
      case SCRIPTED:
        value = scriptedNext(scriptNextSerialized());
        break;
      default:
        value = nextRandom(type);
        break;
    }
    history.add(value);
    observe((T) value);
    log.debug("Value:" + value);
    return value;
  }

  private T scriptedNext(String serialized) {
    if (!evaluateSerialized(serialized)) {
      throw new IllegalArgumentException("Requested invalid scripted value for variable '" + getName() + "': " + serialized);
    }
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
    return (T) value;
  }


  /**
   * Create next value for the ordered loop algorithm.
   *
   * @param type The datatype to be created.
   * @return A new value in this range.
   */
  private Number nextOrderedLoop(DataType type) {
    Number last = min;
    Number value = null;
    if (!history.isEmpty()) {
      //get the previous value
      last = history.get(history.size() - 1);
    } else {
      return min;
    }
    switch (type) {
      case INT:
        value = last.intValue() + increment.intValue();
        break;
      case LONG:
        value = last.longValue() + increment.longValue();
        break;
      case DOUBLE:
        value = last.doubleValue() + increment.doubleValue();
        break;
      default:
        throw new IllegalArgumentException("Enum type:" + type + " unsupported.");
    }
    if (value.doubleValue() > max.doubleValue()) {
      value = min;
    }
    return value;
  }

  /**
   * Create next value for the optimized random algorithm.
   *
   * @param type The datatype to be created.
   * @return A new value in this range.
   */
  private Number nextOptimizedRandom(DataType type) {
    Number value = null;
    do {
      switch (type) {
        case INT:
          value = cInt(min.intValue(), max.intValue());
          break;
        case LONG:
          value = cLong(min.longValue(), max.longValue());
          break;
        case DOUBLE:
          value = cDouble(min.doubleValue(), max.doubleValue());
          break;
        default:
          throw new IllegalArgumentException("Enum type:" + type + " unsupported.");
      }
    } while (optimizerHistory.contains(value));
    optimizerHistory.add(value);
    if (optimizerHistory.size() == (max.intValue() - min.intValue()) + 1) {
      optimizerHistory.clear();
    }
    return value;
  }

  private Number nextRandom(DataType type) {
    Number value = null;
    switch (type) {
      case INT:
        value = cInt(min().intValue(), max().intValue());
        break;
      case LONG:
        value = cLong(min().longValue(), max().longValue());
        break;
      case DOUBLE:
        value = cDouble(min().doubleValue(), max().doubleValue());
        break;
      default:
        throw new IllegalArgumentException("Enum type:" + type + " unsupported.");
    }
    return value;
  }

  private Number nextBoundaryScan() {
    return boundary.next();
  }

  /**
   * Generates a new double value in this value range.
   *
   * @return Generated input value.
   */
  public double nextDouble() {
    return next(DataType.DOUBLE).doubleValue();
  }

  /**
   * Generates a new long value in this value range.
   *
   * @return Generated input value.
   */
  public int nextInt() {
    return next(DataType.INT).intValue();
  }

  /**
   * Generates a new long value in this value range.
   *
   * @return Generated input value.
   */
  public long nextLong() {
    return next(DataType.LONG).longValue();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ValueRange that = (ValueRange) o;

    if (increment != null ? !increment.equals(that.increment) : that.increment != null) return false;
    if (max != null ? !max.equals(that.max) : that.max != null) return false;
    if (min != null ? !min.equals(that.min) : that.min != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = min != null ? min.hashCode() : 0;
    result = 31 * result + (max != null ? max.hashCode() : 0);
    result = 31 * result + (increment != null ? increment.hashCode() : 0);
    return result;
  }

  @Override
  public boolean evaluate(Number value) {
    boolean high = value.doubleValue() <= max.doubleValue();
    boolean low = value.doubleValue() >= min.doubleValue();
    return high && low;
  }

  @Override
  public boolean evaluateSerialized(String item) {
    double value = 0;
    try {
      value = Double.parseDouble(item);
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return false;
    }
    return evaluate(value);
  }

  @Override
  public Collection<T> getOptions() {
    int n = (int) Math.round((max.doubleValue() - min.doubleValue()) / increment.doubleValue());
    if (n > 1000) {
      throw new IllegalStateException("Currently only 1000 values in coverage are supported. You request " + n + ".");
    }
    log.debug("Number of options:" + n);
    Number min = this.min;
    Number max = this.max;
    Collection<T> options = new ArrayList<>();
    while (max.doubleValue() >= min.doubleValue()) {
      T value = null;
      switch (type) {
        case INT:
          value = (T) new Integer(min.intValue());
          min = min.intValue() + increment.intValue();
          break;
        case LONG:
          value = (T) new Long(min.longValue());
          min = min.longValue() + increment.longValue();
          break;
        case DOUBLE:
          value = (T) new Double(min.doubleValue());
          min = min.doubleValue() + increment.doubleValue();
          break;
        default:
          throw new IllegalArgumentException("Enum type:" + type + " unsupported.");
      }
      options.add(value);
    }
    return options;
  }

  @Override
  public void enableGUI() {
    gui = new ValueRangeGUI(this);
  }

  @Override
  public String toString() {
    return "min (" + min + "), max (" + max + ')';
  }
}
