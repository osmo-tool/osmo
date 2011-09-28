package osmo.tester.scripter;

import org.junit.Test;
import osmo.common.TestUtils;
import osmo.tester.scripter.robotframework.Scripter;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;

/**
 * @author Teemu Kanstren
 */
public class RobotFrameworkTests {
  @Test
  public void twoVariablesTwoTestsTwoSteps() {
    Scripter scripter = new Scripter(2);
    scripter.setTestLibrary("SeleniumLibrary");
    scripter.addVariable("var1", "hello");
    scripter.addVariable("var2", "world");
    scripter.startTest("test1");
    scripter.addStep("step1-1", "s1-1 p1", "s1-1 p2");
    scripter.addStep("step1-2", "s1-2 p1", "s1-2 p2");
    scripter.startTest("test2");
    scripter.addStep("step2-1", "s2-1 p1", "s2-1 p2");
    scripter.addStep("step2-2", "s2-2 p1", "s2-2 p2");
    String script = scripter.createScript();
    script = unifyLineSeparators(script, "\n");
    String expected = TestUtils.getResource(RobotFrameworkTests.class, "expected1.html");
    expected = unifyLineSeparators(expected, "\n");
    assertEquals("Generated script", expected, script);
  }
}
