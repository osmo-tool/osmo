package osmo.tester.examples.calendar.scripter.offline;

import osmo.tester.examples.calendar.testapp.CalendarApplication;
import osmo.tester.examples.calendar.testapp.CalendarEvent;
import osmo.tester.examples.calendar.testapp.CalendarTask;
import osmo.tester.examples.calendar.testapp.CalendarUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.Assert.*;

/**
 * A test library for robot framework to execute test scripts on the calculator example.
 *
 * @author Teemu Kanstren
 */
public class CalendarLibrary {
  /** Library scope from robot framework. Specific RF configuration. */
  public static final String ROBOT_LIBRARY_SCOPE = "TEST";
  /** Library version for robot framework. Specific RF configuration. */
  public static final String ROBOT_LIBRARY_VERSION = "1.0.0";
  /** Maps user id to calendar user object. */
  private Map<String, CalendarUser> users = new HashMap<>();
  /** For parsing string formatted dates passed from offline scripter. */
  private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z", Locale.ENGLISH);

  /** This library takes no arguments. */
  public CalendarLibrary() {
  }

  private CalendarApplication getCalendarFor(String uid) {
    CalendarUser user = users.get(uid);
    if (user == null) {
      user = new CalendarUser();
      users.put(uid, user);
    }
    return user.getCalendar();
  }

  /**
   * Parse a Date object from the given String representation.
   *
   * @param time The String to parse.
   * @return The parsed Date object.
   */
  private Date timeFor(String time) {
    try {
      return df.parse(time);
    } catch (ParseException e) {
      throw new IllegalArgumentException("Unable to parse date:" + time, e);
    }
  }

  /**
   * Implements the "Add Task" keyword.
   *
   * @param userId      User for whom the task is to be added.
   * @param time        The time of the task.
   * @param description The description of the task.
   * @return The task id.
   */
  public String addTask(String userId, String time, String description) {
    CalendarApplication calendar = getCalendarFor(userId);
    CalendarTask calendarTask = calendar.addTask(timeFor(time), description);
    return calendarTask.getId();
  }

  /**
   * Implements the "Remove Task" keyword.
   *
   * @param userId The user whose task is to be removed.
   * @param taskId The id of the task that is to be removed.
   */
  public void removeTask(String userId, String taskId) {
    CalendarApplication calendar = getCalendarFor(userId);
    calendar.removeTask(taskId);
  }

  /**
   * Implements the "Add Event" keyword.
   *
   * @param userId      The user for whom the event is to be added.
   * @param start       Start time for the event.
   * @param end         End time for the event.
   * @param description Description of the event.
   * @param location    Location of the event.
   * @return The event id.
   */
  public String addEvent(String userId, String start, String end, String description, String location) {
    CalendarApplication calendar = getCalendarFor(userId);
    CalendarEvent calendarEvent = calendar.addEvent(timeFor(start), timeFor(end), description, location);
    return calendarEvent.getId();
  }

  /**
   * Implements the "Remove Event" keyword.
   *
   * @param userId  The user whose event is to be removed.
   * @param eventId The event that is to be removed.
   */
  public void removeEvent(String userId, String eventId) {
    CalendarApplication calendar = getCalendarFor(userId);
    calendar.removeEvent(eventId, false);
  }

  /**
   * Implements the "Assert User Task Count Is" keyword.
   * Checks that the count of tasks for the given user matches the expected value.
   *
   * @param userId        The user whose tasks need to be checked.
   * @param expectedCount The expected number of tasks.
   */
  public void assertUserTaskCountIs(String userId, String expectedCount) {
    CalendarApplication calendar = getCalendarFor(userId);
    assertEquals("Number of tasks for user " + userId, Integer.parseInt(expectedCount), calendar.getTasks().size());
  }

  /**
   * Implements the "Assert User Has Task" keyword.
   * Checks  that the user has a task with the given properties.
   *
   * @param userId      The user who should have this task.
   * @param time        The time the task should have.
   * @param description The description the task should have.
   */
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
    assertTrue("Expected task not found on calendar:" + userId + ", " + time + ", " + description, found);
  }

  /**
   * Implements the "Assert User Event Count Is" keyword.
   * Checks that the count of events for the given user matches the expected value.
   *
   * @param userId        The user whose events need to be checked.
   * @param expectedCount The expected number of events.
   */
  public void assertUserEventCountIs(String userId, String expectedCount) {
    CalendarApplication calendar = getCalendarFor(userId);
    assertEquals("Number of events for user " + userId, Integer.parseInt(expectedCount), calendar.getEvents().size());
  }

  /**
   * Implements the "Assert User Has Event" keyword.
   * Checks that the given user has an event with the given properties.
   *
   * @param userId      The user who should have the event.
   * @param startTime   The start time of the event.
   * @param endTime     The end time of the event.
   * @param description The description of the event.
   * @param location    The location of the event.
   */
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

  /**
   * Implements the "Remove Nonexistent Task for" keyword.
   * Tries to remove a task that does not exist and checks that an Exception is thrown.
   *
   * @param userId The user for whom to try to remove the task.
   */
  public void removeNonexistentTaskfor(String userId) {
    CalendarApplication calendar = getCalendarFor(userId);
    try {
      calendar.removeTask("no such task");
      fail("Removing a task that does not exist should fail.");
    } catch (Exception e) {
      //expected
    }
  }

  /**
   * Implements the "Remove Nonexistent Event for" keyword.
   * Tries to remove an event that does not exist and checks that an Exception is thrown.
   *
   * @param userId The user for whom to try to remove the task.
   */
  public void removeNonexistentEventFor(String userId) {
    CalendarApplication calendar = getCalendarFor(userId);
    try {
      calendar.removeEvent("no such event", false);
      fail("Removing an event that does not exist should fail.");
    } catch (Exception e) {
      //expected
    }
  }

  /**
   * Implements the "Link Event To User" keyword.
   * Links an existing event to a user that is not the organizer.
   *
   * @param targetUserId The user whom to link the event to.
   * @param sourceUserId The user who has the event already (organizer).
   * @param eventId      The event to be linked.
   */
  public void linkEventToUser(String targetUserId, String sourceUserId, String eventId) {
    CalendarApplication calendarTo = getCalendarFor(targetUserId);
    CalendarApplication calendarFrom = getCalendarFor(sourceUserId);
//    System.out.println("attached event:"+event);
    calendarTo.attach(calendarFrom.getEvent(eventId));
  }
}
