package osmo.tester.reporting.jenkins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a separate test case for Jenkins reports.
 *
 * @author Teemu Kanstren
 */
public class JenkinsTest {
  /** The set of steps in this test case. */
  private List<JenkinsStep> steps = new ArrayList<>();
  /** Provides unique id values. */
  private static AtomicInteger nextId = new AtomicInteger(0);
  /** ID for this test case. */
  private final int id;
  private boolean testing = false;

  /** Reset test ID's for testing. */
  public static void resetId() {
    nextId.set(0);
  }

  public JenkinsTest(boolean testing) {
    id = nextId.incrementAndGet();
    this.testing = testing;
  }

  /**
   * Add a step to this test case, in order.
   *
   * @param step The step to add.
   */
  public void add(JenkinsStep step) {
    steps.add(step);
  }

  /**
   * The name of a test case is "TestN" where N is the test id.
   *
   * @return The name.
   */
  public String getName() {
    return "Test" + id;
  }

  /**
   * Provides a list of class names for model objects from which the different test steps have been executed.
   *
   * @return The string list.
   */
  public String getClassName() {
    Collection<JenkinsStep> mySteps = new LinkedHashSet<>();
    mySteps.addAll(steps);
    Collection<String> name = new LinkedHashSet<>();
    for (JenkinsStep step : mySteps) {
      name.add(step.getClassName());
    }
    return name.toString();
  }

  /**
   * The duration of the test case, equal to sum of duration for all steps.
   *
   * @return Test duration in seconds as string with 2 decimals.
   */
  public String getDuration() {
    if (steps.size() == 0) {
      return "!test has no steps!";
    }
    if (testing) {
      return "0.00";
    }
    JenkinsStep first = steps.get(0);
    JenkinsStep last = steps.get(steps.size() - 1);
    long startTime = first.getStartTime();
    long endTime = last.getEndTime();
    long duration = endTime - startTime;
    double seconds = duration / 1000d;
    return String.format(Locale.US, "%.2f", seconds);
  }

  public List<JenkinsStep> getSteps() {
    return steps;
  }
}
