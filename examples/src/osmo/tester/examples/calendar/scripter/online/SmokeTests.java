package osmo.tester.examples.calendar.scripter.online;

import org.junit.Test;
import osmo.tester.examples.calendar.testapp.CalendarApplication;
import osmo.tester.examples.calendar.testapp.CalendarTask;
import osmo.tester.examples.calendar.testapp.CalendarUser;

import java.util.Calendar;

import static junit.framework.Assert.*;

/** @author Teemu Kanstren */
public class SmokeTests {
  @Test
  public void addTask() {
    CalendarUser user = new CalendarUser();
    CalendarApplication calendar = user.getCalendar();
    Calendar time = Calendar.getInstance();
    time.set(2012, Calendar.NOVEMBER, 1);
    CalendarTask calendarTask = calendar.addTask(time.getTime(),"My Little Task");
    assertEquals("Number of tasks", 1, calendar.getTasks().size());
    assertEquals("Task description", "My Little Task", calendarTask.getDescription());
    assertEquals("Task time", time.getTime(), calendarTask.getWhen());
  }
}
