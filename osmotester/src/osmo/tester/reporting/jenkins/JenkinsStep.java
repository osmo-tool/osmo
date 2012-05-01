package osmo.tester.reporting.jenkins;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Locale;

/** @author Teemu Kanstren */
public class JenkinsStep {
  private final String className;
  private final String name;
  private long startTime;
  private long endTime;
  private Exception error = null;

  public JenkinsStep(String className, String name) {
    this.className = className;
    this.name = name;
  }

  public String getDuration() {
    long duration = endTime - startTime;
    double seconds = duration / 1000d;
    return String.format(Locale.US, "%.2f", seconds);
  }

  public void start() {
    startTime = System.currentTimeMillis();
  }

  public void end() {
    endTime = System.currentTimeMillis();
  }

  public String getClassName() {
    return className;
  }

  public String getName() {
    return name;
  }
  
  public String getError() {
    if (error == null) {
      return null;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
    PrintStream psOut = new PrintStream(out);
    error.printStackTrace(psOut);
    return out.toString();
  }

  /**
   * For testing only.
   * 
   * @param startTime Time (in milliseconds) when step execution was started.
   */
  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  /**
   * For testing only.
   * 
   * @param endTime Time (in milliseconds) when step execution ended.
   */
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
