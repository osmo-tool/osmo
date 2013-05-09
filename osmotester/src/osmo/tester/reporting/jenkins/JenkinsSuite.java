package osmo.tester.reporting.jenkins;

import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestCaseStep;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Represents a test suite for generating Jenkins compatible test reports (in JUnit report format).
 *
 * @author Teemu Kanstren
 */
public class JenkinsSuite {
  /** Suite name. */
  private final String name;
  /** Time when the suite execution was started, milliseconds in Java time format. */
  private long startTime;
  /** Time when the suite execution was ended, milliseconds in Java time format. */
  private long endTime;
  /** The tests in the suite. In order of execution. */
  private Collection<JenkinsTest> tests = new ArrayList<>();
  /** Output stream for system.out in test execution. */
  private ByteArrayOutputStream out;
  /** Output stream for system.err in test execution. */
  private ByteArrayOutputStream err;
  /** Temporarily holds the original system.out while tests are executed. */
  private PrintStream oldOut;
  /** Temporarily holds the original system.err while tests are executed. */
  private PrintStream oldErr;
  /** Captures test system.out prints. */
  private PrintStream newOut;
  /** Captures test system.err prints. */
  private PrintStream newErr;
  /** For formatting date strings. */
  public static DateFormat format = DateFormat.getDateTimeInstance();
  /** For testing only. Overrider normal start time if defined. */
  private Long testStartTime = null;
  /** For testing only. Overrider normal end time if defined. */
  private Long testEndTime = null;
  /** If we are testing ourselves we report 0 for duration to get deterministic test cases. */ 
  private boolean testing = false;

  public JenkinsSuite(String name, boolean testing) {
    this.name = name;
    this.testing = testing;
    out = new ByteArrayOutputStream(1000);
    newOut = new PrintStream(out);
    err = new ByteArrayOutputStream(1000);
    newErr = new PrintStream(err);
  }

  /** Suite execution starts, initialize everything. */
  public void start() {
    //need to reset test id's for unit testing
    JenkinsTest.resetId();
    startTime = System.currentTimeMillis();
    oldOut = System.out;
    oldErr = System.err;
    System.setOut(newOut);
    System.setErr(newErr);
  }

  /** Suite execution finished, restore system state and store end time. */
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

  public long getStartMillis() {
    if (testStartTime != null) {
      return testStartTime;
    }
    return startTime;
  }

  public long getEndMillis() {
    if (testEndTime != null) {
      return testEndTime;
    }
    return endTime;
  }

  /**
   * Provides suite duration as string representing seconds with 2 decimals.
   *
   * @return The duration string.
   */
  public String getDuration() {
    long duration = getEndMillis() - getStartMillis();
    double seconds = duration / 1000d;
    return String.format(Locale.US, "%.2f", seconds);
  }

  /**
   * Adds a test case to this suite.
   *
   * @param test The executed test.
   */
  public void add(TestCase test) {
    JenkinsTest newTest = new JenkinsTest(testing);
    JenkinsStep.resetId();
    List<TestCaseStep> steps = test.getSteps();
    for (TestCaseStep step : steps) {
      //the name of the model object from which the test step was executed
      String className = step.getModelObjectName();
      //the name of the test step that was executed
      String stepName = step.getName();
      //add the step to the jenkins test
      JenkinsStep jenkinsStep = new JenkinsStep(className, newTest, stepName);
      jenkinsStep.setStartTime(step.getStartTime());
      jenkinsStep.setEndTime(step.getEndTime());
      newTest.add(jenkinsStep);
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

  /**
   * Counts the total number of test steps in the suite.
   *
   * @return Total number of steps in all tests.
   */
  public int getStepCount() {
    int count = 0;
    for (JenkinsTest test : tests) {
      count += test.getSteps().size();
    }
    return count;
  }

  /** @return The number of tests in the suite. */
  public int getTestCount() {
    return tests.size();
  }

  /** @return Number of errors in test execution. */
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

  /**
   * JUnit reports make a distinction between failure and error. We only use errors so this is always zero.
   *
   * @return Always 0.
   */
  public int getFailureCount() {
    return 0;
  }

  /** @return Suite test time formatted as a string. */
  public String getStartTime() {
    return format.format(new Date(getStartMillis()));
  }

  /**
   * For testing only.
   *
   * @param startTime Test value for deterministic output.
   */
  public void setStartTime(long startTime) {
    this.testStartTime = startTime;
  }

  /**
   * For testing only.
   *
   * @param endTime Test value for deterministic output.
   */
  public void setEndTime(long endTime) {
    this.testEndTime = endTime;
  }
}
