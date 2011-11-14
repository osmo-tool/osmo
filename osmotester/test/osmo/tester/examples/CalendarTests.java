package osmo.tester.examples;

import junit.framework.AssertionFailedError;
import org.junit.Before;
import org.junit.Test;
import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.scripter.online.OnlineScripter;
import osmo.tester.examples.calendar.testmodel.CalendarBaseModel;
import osmo.tester.examples.calendar.testmodel.CalendarErrorHandlingModel;
import osmo.tester.examples.calendar.testmodel.CalendarFailureModel;
import osmo.tester.examples.calendar.testmodel.CalendarOracleModel;
import osmo.tester.examples.calendar.testmodel.CalendarOverlappingModel;
import osmo.tester.examples.calendar.testmodel.CalendarParticipantModel;
import osmo.tester.examples.calendar.testmodel.CalendarTaskModel;
import osmo.tester.examples.calendar.testmodel.ModelState;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import static junit.framework.Assert.*;
import static osmo.common.TestUtils.*;

/** @author Teemu Kanstren */
public class CalendarTests {
  private OSMOTester osmo = null;
  private PrintStream out;
  private ByteArrayOutputStream bos;

  @Before
  public void testSetup() {
    osmo = new OSMOTester();
    osmo.setSeed(111);
    bos = new ByteArrayOutputStream();
    out = new PrintStream(bos);
  }

  @Test
  public void baseModelOnline() {
    ModelState state = new ModelState();
    CalendarScripter scripter = new OnlineScripter();
    osmo.addModelObject(new CalendarBaseModel(state, scripter, out));
    generateAndAssertOutput("expected-base-online.txt");
    scripter.write();
  }

  @Test
  public void fullModelOnline() {
    ModelState state = new ModelState();
    CalendarScripter scripter = new OnlineScripter();
    osmo.addModelObject(new CalendarBaseModel(state, scripter, out));
    osmo.addModelObject(new CalendarOracleModel(state, scripter, out));
    osmo.addModelObject(new CalendarTaskModel(state, scripter, out));
    osmo.addModelObject(new CalendarOverlappingModel(state, scripter, out));
    osmo.addModelObject(new CalendarParticipantModel(state, scripter, out));
    osmo.addModelObject(new CalendarErrorHandlingModel(state, scripter, out));
    generateAndAssertOutput("expected-full-online.txt");
    scripter.write();
  }

  private void generateAndAssertOutput(String expectedFile) {
    osmo.generate();
    String expected = getResource(CalendarTests.class, expectedFile);
    expected = unifyLineSeparators(expected, "\n");
    String actual = bos.toString();
    actual = unifyLineSeparators(actual, "\n");
    assertEquals(expected, actual);
  }

  @Test
  public void failureModelOnline() {
    ModelState state = new ModelState();
    CalendarScripter scripter = new OnlineScripter();
    osmo.addModelObject(new CalendarBaseModel(state, scripter));
    osmo.addModelObject(new CalendarOracleModel(state, scripter));
    osmo.addModelObject(new CalendarTaskModel(state, scripter));
    osmo.addModelObject(new CalendarOverlappingModel(state, scripter));
    osmo.addModelObject(new CalendarParticipantModel(state, scripter));
    osmo.addModelObject(new CalendarErrorHandlingModel(state, scripter));
    osmo.addModelObject(new CalendarFailureModel(state, scripter));
    try {
      osmo.generate();
      fail("FailureModel should fail assertions");
    } catch (Exception e) {
      Throwable cause = e.getCause();
      assertEquals("Failure type", InvocationTargetException.class, cause.getClass());
      assertEquals("Failure type2", AssertionFailedError.class, cause.getCause().getClass());
      //expected
    }
    scripter.write();
  }

  @Test
  public void baseModel() {
    ModelState state = new ModelState();
    OfflineScripter scripter = new OfflineScripter("tests.html");
    osmo.addModelObject(new CalendarBaseModel(state, scripter));
    generateAndAssertScript(scripter, "expected-base-offline.txt");

  }

  private void generateAndAssertScript(OfflineScripter scripter, String expectedFile) {
    osmo.generate();
    String actual = scripter.getScript();
    String expected = getResource(CalendarTests.class, expectedFile);
    expected = unifyLineSeparators(expected, "\n");
    actual = unifyLineSeparators(actual, "\n");
    assertEquals(expected, actual);
  }

  @Test
  public void fullModel() {
    ModelState state = new ModelState();
    OfflineScripter scripter = new OfflineScripter("tests.html");
    osmo.addModelObject(new CalendarBaseModel(state, scripter));
    osmo.addModelObject(new CalendarOracleModel(state, scripter));
    osmo.addModelObject(new CalendarTaskModel(state, scripter));
    osmo.addModelObject(new CalendarOverlappingModel(state, scripter));
    osmo.addModelObject(new CalendarParticipantModel(state, scripter));
    osmo.addModelObject(new CalendarErrorHandlingModel(state, scripter));
    osmo.addModelObject(new CalendarFailureModel(state, scripter));
    generateAndAssertScript(scripter, "expected-full-offline.txt");
  }
}
