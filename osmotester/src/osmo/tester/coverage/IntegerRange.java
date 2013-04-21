package osmo.tester.coverage;

/**
 * Represents a range of values for a {@link IntegerRange} coverage metric.
 *
 * @author Teemu Kanstren
 */
public class IntegerRange {
  /** Name for this range. For example, 1-1 = "one" or 2-N="many" */
  public final String name;
  /** The range minimum value. */
  public final int min;
  /** The range maximum value. */
  public final int max;

  public IntegerRange(int min, int max, String name) {
    this.min = min;
    this.max = max;
    if (min > max) {
      throw new IllegalArgumentException("Minimum cannot be bigger than maximum:" + min + " > " + max);
    }
    this.name = name;
  }

  @Override
  public String toString() {
    return name + " (" + min + "-" + max + ")";
  }

  public String getName() {
    return name;
  }

}
