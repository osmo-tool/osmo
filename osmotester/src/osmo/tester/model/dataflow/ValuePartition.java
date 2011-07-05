package osmo.tester.model.dataflow;

/**
 * @author Teemu Kanstren
 */
public class ValuePartition {
  private double min = Double.MIN_VALUE;
  private double max = Double.MAX_VALUE;

  public ValuePartition() {
  }

  public ValuePartition(Number min, Number max) {
    this.min = min.doubleValue();
    this.max = max.doubleValue();
  }

  public Number min() {
    return min;
  }

  public void setMin(Number min) {
    this.min = min.doubleValue();
  }

  public Number max() {
    return max;
  }

  public void setMax(Number max) {
    this.max = max.doubleValue();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ValuePartition interval = (ValuePartition) o;

    if (Double.compare(interval.max, max) != 0) return false;
    if (Double.compare(interval.min, min) != 0) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = min != +0.0d ? Double.doubleToLongBits(min) : 0L;
    result = (int) (temp ^ (temp >>> 32));
    temp = max != +0.0d ? Double.doubleToLongBits(max) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  public boolean contains(Number value) {
    double d = value.doubleValue();
    return d <= max && d >= min;
  }

  @Override
  public String toString() {
    return "min (" + min + "), max (" + max + ')';
  }
}
