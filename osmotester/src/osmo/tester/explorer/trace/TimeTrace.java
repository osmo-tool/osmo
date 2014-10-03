package osmo.tester.explorer.trace;

/** 
 * The time it took to explore a specific test step for a specific test case.
 * Tracked by the {@link osmo.tester.explorer.ExplorerAlgorithm}.
 * 
 * @author Teemu Kanstren 
 */
public class TimeTrace {
  /** Index of generated test. */
  private final int test;
  /** Index of step in the test case. */
  private final int step;
  /** The chosen step name (as chosen by explorer for execution). */
  private final String name;
  /** Time it took to explore the step. */
  private final long time;

  public TimeTrace(int test, int step, String name, long time) {
    this.test = test;
    this.step = step;
    this.name = name;
    this.time = time;
  }

  public int getTest() {
    return test;
  }

  public int getStep() {
    return step;
  }

  public String getName() {
    return name;
  }

  public long getTime() {
    return time;
  }
}
