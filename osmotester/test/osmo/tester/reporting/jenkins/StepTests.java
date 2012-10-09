package osmo.tester.reporting.jenkins;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class StepTests {
  private JenkinsStep step;

  @Before
  public void setUp() throws Exception {
    JenkinsTest test = new JenkinsTest();
    step = new JenkinsStep(StepTests.class.getName(), test, "duration");
  }

  @Test
  public void durationSeconds() {
    step.setStartTime(1000);
    step.setEndTime(5000);
    assertEquals("Duration formatting for 4000 ms", "4.00", step.getDuration());
  }

  @Test
  public void durationSecondFractions() {
    step.setStartTime(1000);
    step.setEndTime(5111);
    assertEquals("Duration formatting for 4111 ms", "4.11", step.getDuration());
  }

  @Test
  public void classNameFormatting() {
    assertEquals("Classname formatted", "osmo.tester.reporting.jenkins.StepTests", step.getClassName());
  }

  @Test
  public void errorFormatting() {
    try {
      throw new IllegalArgumentException("Test error");
    } catch (IllegalArgumentException e) {
      step.setError(e);
      String expected = "java.lang.IllegalArgumentException: Test error";
      assertTrue("Error formatted", step.getError().startsWith(expected));
    }
  }
}
