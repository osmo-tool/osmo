package osmo.tester.reporting.jenkins;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a test step in a Jenkins report.
 *
 * @author Teemu Kanstren
 */
public class JenkinsStep {
  /** Name of the class for the model object from which this step was executed. */
  private final String className;
  /** Name of the test step. */
  private final String name;
  /** When the step was started. */
  private long startTime;
  /** When the step ended. */
  private long endTime;
  /** Was there an error executing this step? */
  private Exception error = null;
  /** Next ID for this step. */
  private static final AtomicInteger nextId = new AtomicInteger(0);

  public JenkinsStep(String className, JenkinsTest parent, String name) {
    this.className = className;
    int id = nextId.incrementAndGet();
    this.name = parent.getName() + "_step_" + id + "_" + name;
  }

  public static void resetId() {
    nextId.set(0);
  }

  /**
   * Duration for step execution in seconds with two decimal precision.
   *
   * @return Step duration.
   */
  public String getDuration() {
    long duration = endTime - startTime;
    double seconds = duration / 1000d;
    return String.format(Locale.US, "%.2f", seconds);
  }

  public String getClassName() {
    return className;
  }

  public String getName() {
    return name;
  }

  /**
   * If there was an error in executing this step, return a stacktrace for the error.
   *
   * @return Stacktrace or null.
   */
  public String getError() {
    if (error == null) {
      return null;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream psOut = new PrintStream(out);
    error.printStackTrace(psOut);
    return out.toString();
  }

  /** @param startTime Time (in milliseconds) when step execution was started. */
  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  /** @param endTime Time (in milliseconds) when step execution ended. */
  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public void setError(Exception error) {
    this.error = error;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }
}
