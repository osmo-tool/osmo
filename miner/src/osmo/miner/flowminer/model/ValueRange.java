package osmo.miner.flowminer.model;

/** @author Teemu Kanstren */
public class ValueRange {
  private final String name;
  private Integer min = null;
  private Integer max = null;
  private boolean valid = true;

  public ValueRange(String name) {
    this.name = name;
  }

  public void check(String value) {
    try {
      int i = Integer.parseInt(value);
      if (min == null || i < min) {
        min = i;
      }
      if (max == null || i > max) {
        max = i;
      }
    } catch (NumberFormatException e) {
      valid = false;
    }
  }

  public boolean isValid() {
    return valid;
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }

  public String getName() {
    return name;
  }
}
