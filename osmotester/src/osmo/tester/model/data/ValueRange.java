package osmo.tester.model.data;

import osmo.common.Logger;
import osmo.tester.OSMOConfiguration;
import osmo.tester.gui.manualdrive.ValueRangeGUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Defines a value range with a minimum and maximum values. Generates input from this range, including min and max.
 * Supports subclasses of {@link Number}, specifically {@link Integer}, {@link Long}, and {@link Double}.
 * Notice that due to limitations of the Java generics type system, you have to either provide the type
 * as an explicit argument to the constructor or otherwise the type to be generated will be based on the
 * type of object passed as the "min" value of the range.
 * <p>
 * To get integers do either
 * {@code new ValueRange<Integer>(1,2);} or
 * {@code new ValueRange<Integer>(Integer.class, 1, 2);}
 * <p>
 * to get long values do either
 * {@code new ValueRange<Long>(1l,2l);} or
 * {@code new ValueRange<Long>(Long.class, 1, 2);}
 * <p>
 * to get double values do either
 * {@code new ValueRange<Double>(1d,2d);} or
 * {@code new ValueRange<Double>(Double.class, 1, 2);}
 * <p>
 * Omitting the type information will result in generation of integers.
 * It is also possible to use any of the specific functions such as nextInt(), nextLong() and nextDouble()
 * regardless of configured type.
 *
 * @author Teemu Kanstren
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
  /** Balancing strategy selection set. */
  protected ValueSet<T> balancingChoices = new ValueSet<>();
  /** The actual type of data to be generated. */
  private DataType type;
  /** Handles boundary scan data generation strategy. */
  private Boundary boundary;
  private Number choice = null;

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
      setType(DataType.INT);
    } else if (type.equals(Long.class)) {
      setType(DataType.LONG);
    } else {
      setType(DataType.DOUBLE);
    }
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
      setType(DataType.INT);
    } else if (min instanceof Long) {
      setType(DataType.LONG);
    } else {
      setType(DataType.DOUBLE);
    }
    boundary = new Boundary(this.type, min, max);
  }

  @Override
  public void setSeed(long seed) {
    super.setSeed(seed);
    balancingChoices.setSeed(seed);
  }

  private void setType(DataType type) {
    this.type = type;
    boundary = new Boundary(this.type, min, max);
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

  private void pre() {
    if (rand == null) throw new IllegalStateException("You need to set seed before using data objects");
    choice = null;
    OSMOConfiguration.check(this);
    if (gui != null) {
      choice = (T) gui.next();
    }
  }
  
  private void post() {
    history.add(choice);
    record((T) choice);
    log.d("Value:" + choice);
  }

  /**
   * Create next value for the ordered loop.
   *
   * @return Chosen value.
   */
  public T loop() {
    pre();
    if (choice == null) {
      Number last = min;
      if (!history.isEmpty()) {
        //get the previous value
        last = history.get(history.size() - 1);
      } else {
        choice = min;
        post();
        return (T)choice;
      }
      switch (type) {
        case INT:
          choice = last.intValue() + increment.intValue();
          break;
        case LONG:
          choice = last.longValue() + increment.longValue();
          break;
        case DOUBLE:
          choice = last.doubleValue() + increment.doubleValue();
          break;
        default:
          throw new IllegalArgumentException("Enum type:" + type + " unsupported.");
      }
      if (choice.doubleValue() > max.doubleValue()) {
        choice = min;
      }
    }
    post();
    return (T)choice;
  }

  /**
   * Create next value for the balancing strategy. Note that this creates a list of all options so really large
   * sets on possible input will eat big memory.
   *
   * @return A new value in this range.
   */
  public T balanced() {
    pre();
    if (choice == null) {
      if (balancingChoices.size() == 0) balancingChoices.addAll(getOptions());
      choice = balancingChoices.removeRandom();
    }
    post();
    return (T)choice;
  }

  public T random() {
    pre();
    if (choice == null) {
      switch (type) {
        case INT:
          choice = rand.nextInt(min().intValue(), max().intValue());
          break;
        case LONG:
          choice = rand.nextLong(min().longValue(), max().longValue());
          break;
        case DOUBLE:
          choice = rand.nextDouble(min().doubleValue(), max().doubleValue());
          break;
        default:
          throw new IllegalArgumentException("Enum type:" + type + " unsupported.");
      }
    }
    post();
    return (T)choice;
  }

  public T boundaryIn() {
    pre();
    if (choice == null) {
      choice = boundary.in();
    }
    post();
    return (T)choice;
  }

  public T boundaryOut() {
    pre();
    if (choice == null) {
      choice = boundary.out();
    }
    post();
    return (T)choice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ValueRange that = (ValueRange) o;

    if (increment != null ? !increment.equals(that.increment) : that.increment != null) return false;
    if (max != null ? !max.equals(that.max) : that.max != null) return false;
      return min != null ? min.equals(that.min) : that.min == null;
  }

  @Override
  public int hashCode() {
    int result = min != null ? min.hashCode() : 0;
    result = 31 * result + (max != null ? max.hashCode() : 0);
    result = 31 * result + (increment != null ? increment.hashCode() : 0);
    return result;
  }

  @Override
  public Collection<T> getOptions() {
    int n = (int) Math.round((max.doubleValue() - min.doubleValue()) / increment.doubleValue());
    if (n > 1000) {
      throw new IllegalStateException("Currently only 1000 values in coverage are supported. You request " + n + ".");
    }
    log.d("Number of options:" + n);
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
    if (gui != null) return;
    gui = new ValueRangeGUI(this);
  }

  @Override
  public String toString() {
    return "ValueRange{ name=" +getName()+", " + 
            "min=" + min +
            ", max=" + max +
            '}';
  }
}
