package osmo.tester.examples.calendar.scripter.offline;

import osmo.tester.examples.calendar.testapp.CalendarApplication;
import osmo.tester.examples.calendar.testapp.CalendarEvent;
import osmo.tester.examples.calendar.testapp.CalendarTask;
import osmo.tester.examples.calendar.testapp.CalendarUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * @author Teemu Kanstren
 */
public class CalculatorLibrary {
  /** Library scope from robot framework. */
  public static final String ROBOT_LIBRARY_SCOPE = "TEST";
  /** Library version for robot framework. */
  public static final String ROBOT_LIBRARY_VERSION = "1.0.0";
  private Map<String, CalendarUser> users = new HashMap<String, CalendarUser>();
  private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z", Locale.ENGLISH);

  /**
   * This library takes no arguments.
   */
  public CalculatorLibrary() {
  }

  private CalendarApplication getCalendarFor(String uid) {
    CalendarUser user = users.get(uid);
    if (user == null) {
      user = new CalendarUser();
      users.put(uid, user);
    }
    return user.getCalendar();
  }

  private Date timeFor(String time) {
    try {
      return df.parse(time);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Unable to parse date:"+time, e);
    }
  }

  public String addTask(String userId, String time, String description) {
    CalendarApplication calendar = getCalendarFor(userId);
    CalendarTask calendarTask = calendar.addTask(timeFor(time), description);
    return calendarTask.getId();
  }

  public void removeTask(String userId, String taskId) {
    CalendarApplication calendar = getCalendarFor(userId);
    calendar.removeTask(taskId);
  }

  public String addEvent(String userId, String start, String end, String description, String location) {
    CalendarApplication calendar = getCalendarFor(userId);
    CalendarEvent calendarEvent = calendar.addEvent(timeFor(start), timeFor(end), description, location);
    return calendarEvent.getId();
  }

  public void removeEvent(String userId, String eventId) {
    CalendarApplication calendar = getCalendarFor(userId);
    calendar.removeEvent(eventId, false);
  }

  public void assertUserTaskCountIs(String userId, String expectedCount) {
    CalendarApplication calendar = getCalendarFor(userId);
    assertEquals("Number of tasks for user "+userId, Integer.parseInt(expectedCount), calendar.getTasks().size());
  }

  public void assertUserHasTask(String userId, String time, String description) {
    CalendarApplication calendar = getCalendarFor(userId);
    Date when = timeFor(time);
    Collection<CalendarTask> tasks = calendar.getTasks();
    boolean found = false;
    for (CalendarTask task : tasks) {
      if (task.getWhen().equals(when) && task.getDescription().equals(description)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected task not found on calendar:" + userId + ", "+time+", " + description, found);
  }
  
  public void assertUserEventCountIs(String userId, String expectedCount) {
    CalendarApplication calendar = getCalendarFor(userId);
    assertEquals("Number of events for user " + userId, Integer.parseInt(expectedCount), calendar.getEvents().size());
  }

  public void assertUserHasEvent(String userId, String startTime, String endTime, String description, String location) {
    CalendarApplication calendar = getCalendarFor(userId);
    Date start = timeFor(startTime);
    Date end = timeFor(endTime);
    Collection<CalendarEvent> events = calendar.getEvents();
    boolean found = false;
    for (CalendarEvent event : events) {
      if (event.getStart().equals(start) && event.getEnd().equals(end) && event.getDescription().equals(description)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected event not found on calendar:" + userId + ", " + start + ", " + end + ", " + description, found);
  }

  public void removeNonexistentTaskfor(String userId) {
    CalendarApplication calendar = getCalendarFor(userId);
    try {
      calendar.removeTask("no such task");
      fail("Removing a task that does not exist should fail.");
    } catch (Exception e) {
      //expected
    }
  }

  public void removeNonexistentEventFor(String userId) {
    CalendarApplication calendar = getCalendarFor(userId);
    try {
      calendar.removeEvent("no such event", false);
      fail("Removing an event that does not exist should fail.");
    } catch (Exception e) {
      //expected
    }
  }

  public void linkEventToUser(String targetUserId, String sourceUserId, String eventId) {
    CalendarApplication calendarTo = getCalendarFor(targetUserId);
    CalendarApplication calendarFrom = getCalendarFor(sourceUserId);
//    System.out.println("attached event:"+event);
    calendarTo.attach(calendarFrom.getEvent(eventId));
  }
}
