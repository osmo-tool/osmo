package osmo.tester.scenario;

/** 
 * Defines a test generation slice. 
 * That is minimum number of times to have a step appear. Zero for no definition.
 * And maximum number of times to have a step appear. Zero for no definition.
 * Possible startup sequence is not counted for a slice.
 * 
 * @author Teemu Kanstren 
 */
public class Slice {
  /** Name step to slice. */
  private final String stepName;
  /** Minimum number of times it has to appear. */
  private final int min;
  /** Maximum number of times it can appear. */
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
