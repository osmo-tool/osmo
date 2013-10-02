package osmo.tester.scenario;

/** @author Teemu Kanstren */
public class Slice {
  private final String stepName;
  private final int min;
  private final int max;

  public Slice(String stepName, int min, int max) {
    this.stepName = stepName;
    this.min = min;
    this.max = max;
  }

  public String getStepName() {
    return stepName;
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }
}
