package osmo.tester.dsm;

/** @author Teemu Kanstren */
public class StepRequirement {
  private final String step;
  private Integer max = null;
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
