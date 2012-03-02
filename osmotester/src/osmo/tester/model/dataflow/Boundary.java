package osmo.tester.model.dataflow;

import java.util.List;

/**
 * Implements boundary scanning for given numeric boundary values.
 * To use, define min and max of boundaries, as well the increment of values for the boundary scan.
 * This is mainly used internally by the ValueRange data object, but can also be used directly if needed.
 * NOTE: By default this only generates 5 boundary values for each boundary. Set the count to different
 * values to have more values.
 * <p/>
 * This can also be re-used by re-setting the properties, and calling init() again.
 *
 * @author Teemu Kanstren
 */
public class Boundary {
  /** The set of values to be provided, initialized and startup on call to init() */
  private ValueSet<Number> values = new ValueSet<>(DataGenerationStrategy.ORDERED_LOOP);
  /** How many values will be generated for each boundary. */
  private int count = 5;
  /** The value by which the boundary is incremented / decremented in the scan. */
  private Number increment = 1;
  /** The numeric data type. */
  private final DataType type;
  /** Lower bound. */
  private final Number min;
  /** Upper bound. */
  private final Number max;

  public Boundary(DataType type, Number min, Number max) {
    this.type = type;
    this.min = min;
    this.max = max;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public Number getIncrement() {
    return increment;
  }

  public void setIncrement(Number increment) {
    this.increment = increment;
  }

  private void init() {
    Number addReduce = increment;
    //first we add the minimum bound to test set
    values.add(min);
    //and the upper bound
    values.add(max);
    for (int i = 0; i < count; i++) {
      switch (type) {
        case INT:
          //then we add minimum +1 and miminum -1 (or whatever the increment value)
          //and in subsequent loops we increase this to +2, -2, +3, -3, and so on
          values.add(min.intValue() + addReduce.intValue());
          values.add(max.intValue() + addReduce.intValue());
          //and do the same for maximum bound
          values.add(min.intValue() - addReduce.intValue());
          values.add(max.intValue() - addReduce.intValue());
          //update the increment for next loop
          addReduce = addReduce.intValue() + increment.intValue();
          break;
        //the following do the same as above but with long and double data types
        case LONG:
          values.add(min.longValue() + addReduce.longValue());
          values.add(max.longValue() + addReduce.longValue());
          values.add(min.longValue() - addReduce.longValue());
          values.add(max.longValue() - addReduce.longValue());
          addReduce = addReduce.longValue() + increment.longValue();
          break;
        case DOUBLE:
          values.add(min.doubleValue() + addReduce.doubleValue());
          values.add(max.doubleValue() + addReduce.doubleValue());
          values.add(min.doubleValue() - addReduce.doubleValue());
          values.add(max.doubleValue() - addReduce.doubleValue());
          addReduce = addReduce.doubleValue() + increment.doubleValue();
          break;
      }
    }
  }

  public List<Number> getOptions() {
    return values.getOptions();
  }

  /** @return The next boundary value. */
  public Number next() {
    if (values.size() == 0) {
      init();
    }
    return values.next();
  }
}
