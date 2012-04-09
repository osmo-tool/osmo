package osmo.tester.scripting.slicing;

/**
 * Defines a coverage requirement for test step. Minimum or maximum number it needs/can be covered.
 *
 * @author Teemu Kanstren
 */
public class StepRequirement {
  /** Step (transition) name. */
  private final String step;
  /** Maximum number of times it can be covered. */
  private Integer max = null;
  /** Minimum number of times it must be covered. */
  private Integer min = null;

  public StepRequirement(String step) {
    this.step = step;
  }

  public String getStep() {
    return step;
  }

  public Integer getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public Integer getMin() {
    return min;
  }

  public void setMin(int min) {
    this.min = min;
  }
}
