package osmo.tester.reporting.coverage;

/**
 * Information for how many times a step pair has been taken in test generation.
 * A step pair consists of two steps taken in a sequence in a single test case.
 *
 * @author Teemu Kanstren
 */
public class ValueCount implements Comparable<ValueCount> {
  /** The pair itself. */
  private final String value;
  /** The number of times this pair has been observed in test generation. */
  private final int count;

  public ValueCount(String value, int count) {
    this.value = value;
    this.count = count;
  }

  public String getValue() {
    return value;
  }

  public int getCount() {
    return count;
  }

  @Override
  public int compareTo(ValueCount o) {
    int diff = o.count - count;
    if (diff == 0) {
      diff = value.compareTo(o.value);
    }
    return diff;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ValueCount that = (ValueCount) o;

    if (count != that.count) return false;
      return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    int result = value.hashCode();
    result = 31 * result + count;
    return result;
  }
}
