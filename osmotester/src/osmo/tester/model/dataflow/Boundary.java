package osmo.tester.model.dataflow;

/**
 * @author Teemu Kanstren
 */
public class Boundary {
  private ValueSet<Number> values = new ValueSet<Number>(DataGenerationStrategy.ORDERED_LOOP);
  private int count = 5;
  private Number increment = 1;
  private final DataType type;
  private final Number min;
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
    values.add(min);
    values.add(max);
    for (int i = 0 ; i < count ; i++) {
      switch (type) {
        case INT:
          values.add(min.intValue()+addReduce.intValue());
          values.add(max.intValue()+addReduce.intValue());
          values.add(min.intValue()-addReduce.intValue());
          values.add(max.intValue()-addReduce.intValue());
          addReduce = addReduce.intValue() + increment.intValue();
          break;
        case LONG:
          values.add(min.longValue()+addReduce.longValue());
          values.add(max.longValue()+addReduce.longValue());
          values.add(min.longValue()-addReduce.longValue());
          values.add(max.longValue()-addReduce.longValue());
          addReduce = addReduce.longValue() + increment.longValue();
          break;
        case DOUBLE:
          values.add(min.doubleValue()+addReduce.doubleValue());
          values.add(max.doubleValue()+addReduce.doubleValue());
          values.add(min.doubleValue()-addReduce.doubleValue());
          values.add(max.doubleValue()-addReduce.doubleValue());
          addReduce = addReduce.doubleValue() + increment.doubleValue();
          break;
      }
    }
  }

  public Number next() {
    if (values.size() == 0) {
      init();
    }
    return values.next();
  }
}
