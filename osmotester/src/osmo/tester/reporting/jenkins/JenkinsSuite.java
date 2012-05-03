package osmo.tester.reporting.jenkins;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestStep;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.*;

/** @author Teemu Kanstren */
public class JenkinsSuite {
  private final String name;
  private long startTime;
  private long endTime;
  private Collection<JenkinsTest> tests = new ArrayList<>();
  private ByteArrayOutputStream out;
  private ByteArrayOutputStream err;
  private PrintStream oldOut;
  private PrintStream oldErr;
  private PrintStream newOut;
  private PrintStream newErr;
  public static DateFormat format = DateFormat.getDateTimeInstance();

  public JenkinsSuite(String name) {
    this.name = name;
    out = new ByteArrayOutputStream(1000);
    newOut = new PrintStream(out);
    err = new ByteArrayOutputStream(1000);
    newErr = new PrintStream(err);
  }

  public void start() {
    startTime = System.currentTimeMillis();
    oldOut = System.out;
    oldErr = System.err;
    System.setOut(newOut);
    System.setErr(newErr);
  }

  public void end() {
    endTime = System.currentTimeMillis();
    System.setOut(oldOut);
    System.setErr(oldErr);
  }

  public String getName() {
    return name;
  }

  public String getSystemOut() {
    return out.toString();
  }

  public String getSystemErr() {
    return err.toString();
  }

  public String getDuration() {
    long duration = endTime - startTime;
    double seconds = duration / 1000d;
    return String.format(Locale.US, "%.2f", seconds);
  }

  public void add(TestCase test) {
    JenkinsTest newTest = new JenkinsTest();
    List<TestStep> steps = test.getSteps();
    for (TestStep step : steps) {
      String className = step.getTransition().getTransition().getModelObject().getClass().getName();
      String stepName = step.getTransition().getName();
      newTest.add(new JenkinsStep(className, stepName));
    }
    tests.add(newTest);
  }

  public Collection<JenkinsStep> getSteps() {
    Collection<JenkinsStep> steps = new ArrayList<>();
    for (JenkinsTest test : tests) {
      steps.addAll(test.getSteps());
    }
    return steps;
  }

  public Collection<JenkinsTest> getTests() {
    return tests;
  }

  public int getStepCount() {
    int count = 0;
    for (JenkinsTest test : tests) {
      count += test.getSteps().size();
    }
    return count;
  }

  public int getTestCount() {
    return tests.size();
  }

  public int getErrorCount() {
    int count = 0;
    for (JenkinsTest test : tests) {
      List<JenkinsStep> steps = test.getSteps();
      for (JenkinsStep step : steps) {
        if (step.getError() != null) {
          count++;
        }
      }
    }
    return count;
  }

  public int getFailureCount() {
    return 0;
  }

  public String getStartTime() {
    return format.format(new Date(startTime));
  }

  /**
   * For testing only.
   *
   * @param startTime Test value for deterministic output.
   */
  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  /**
   * For testing only.
   *
   * @param endTime Test value for deterministic output.
   */
  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }
}
