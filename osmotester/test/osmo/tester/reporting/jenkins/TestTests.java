package osmo.tester.reporting.jenkins;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class TestTests {
  private JenkinsTest test;

  @Before
  public void setUp() throws Exception {
    test = new JenkinsTest();
  }

  @Test
  public void classNameNoSteps() {
    String name = test.getClassName();
    assertEquals("Class name formatting", "[]", name);
  }

  @Test
  public void classNameDuplicates() {
    JenkinsTest test = new JenkinsTest();
    test.add(new JenkinsStep("classname1", test, "step1"));
    test.add(new JenkinsStep("classname1", test, "step1"));
    test.add(new JenkinsStep("classname2", test, "step1"));
    test.add(new JenkinsStep("classname2", test, "step1"));
    String name = test.getClassName();
    assertEquals("Class name formatting", "[classname1, classname2]", name);
  }

  @Test
  public void durationNoSteps() {
    assertEquals("Duration with no steps", "!test has no steps!", test.getDuration());
  }

  @Test
  public void durationOneStep() {
    JenkinsTest test = new JenkinsTest();
    JenkinsStep step = new JenkinsStep("class", test, "step");
    step.setStartTime(1000);
    step.setEndTime(2344);
    test.add(step);
    assertEquals("Duration with one step", "1.34", test.getDuration());
  }

  @Test
  public void durationThreeSteps() {
    JenkinsTest test = new JenkinsTest();
    JenkinsStep step1 = new JenkinsStep("class", test, "step");
    step1.setStartTime(1000);
    step1.setEndTime(2000);
    test.add(step1);
    JenkinsStep step2 = new JenkinsStep("class", test, "step");
    step2.setStartTime(2100);
    step2.setEndTime(3100);
    test.add(step2);
    JenkinsStep step3 = new JenkinsStep("class", test, "step");
    step3.setStartTime(3100);
    step3.setEndTime(4444);
    test.add(step3);
    assertEquals("Duration with three steps", "3.44", test.getDuration());
  }
}
