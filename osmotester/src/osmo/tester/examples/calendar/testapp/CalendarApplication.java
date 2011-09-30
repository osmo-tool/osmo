package osmo.tester.examples.calendar.testapp;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

/**
 * An example test application that provides a calendar with the following features
 * -several users, each with their own calendar
 * -create and remove tasks
 * -a task has a time (date), only one participant (the calendar owner), and a description
 * -create and remove events
 * -an event has a start time, end time, one or more participants, a description and a location
 * -linking events from an organizer to other users (simulates invitations)
 * -removing events from organizer removes them from all participants
 *
 * A user is assumed to have only one calendar.
 *
 * @author Teemu Kanstren
 */
public class CalendarApplication {
  /** The server manages links between events, allowing cascading deletes from organizer. */
  private final CalendarServer server = CalendarServer.getServer();
  /** The set of tasks in this calendar. */
  private Collection<CalendarTask> tasks = new HashSet<CalendarTask>();
  /** The set of events in this calendar. */
  private Collection<CalendarEvent> events = new HashSet<CalendarEvent>();
  /** The identifier of the user whose calendar this is. */
  private final String uid;

  /**
   * Creates a calendar for the given user.
   *
   * @param user The user who needs a calendar.
   */
  public CalendarApplication(CalendarUser user) {
    uid = user.getUserId();
  }

  /**
   * Adds a task with the given properties to the calendar.
   *
   * @param date The time of the task.
   * @param description The description of the task.
   * @return The created task.
   */
  public CalendarTask addTask(Date date, String description) {
    CalendarTask task = new CalendarTask(uid, date, description);
    tasks.add(task);
    return task;
  }

  /**
   * Adds a event with the given properties to the calendar.
   *
   * @param start The start time of the event.
   * @param end The end time of the event.
   * @param description The description of the event.
   * @param location The location of the event.
   * @return The create event.
   */
  public CalendarEvent addEvent(Date start, Date end, String description, String location) {
    CalendarEvent event = new CalendarEvent(uid, start, end, description, location);
    events.add(event);
    return event;
  }

  /**
   * Removes the given task from the calendar.
   *
   * @param taskId The identifier for the given task.
   * @throws IllegalArgumentException if the task is not found.
   */
  public void removeTask(String taskId) {
    CalendarTask toRemove = getTask(taskId);
    if (toRemove == null) {
      throw new IllegalArgumentException("Task to remove does not exist:"+taskId);
    }
    tasks.remove(toRemove);
  }

  /**
   * Finds and returns a task matching the given identifier from this calendar.
   *
   * @param taskId The identifier of the task to be found.
   * @return The task or null if no match is found.
   */
  public CalendarTask getTask(String taskId) {
    for (CalendarTask task : tasks) {
      if (task.getId().equals(taskId)) {
        return task;
      }
    }
    return null;
  }

  /**
   * Removes the given event from this calendar.
   *
   * @param eventId The identifier of the event to be removed.
   * @param serverSource Should be true if the request is from the CalendarServer.
   * The server may try a cascading delete over all calendars so it should not fail,
   * while direct calendar remove should be valid and fail if not found.
   */
  public void removeEvent(String eventId, boolean serverSource) {
    CalendarEvent toRemove = getEvent(eventId);
    if (toRemove == null) {
      if (!serverSource) {
        throw new IllegalArgumentException("Event to remove does not exist:"+eventId);
      }
      return;
    }
    if (toRemove.getOrganizer().equals(uid)) {
      server.deleteEvent(uid, eventId);
    }
    events.remove(toRemove);
  }

  public String getUid() {
    return uid;
  }

  /**
   * Find an event matching the given identifier in this calendar.
   *
   * @param eventId The identifier to find.
   * @return The matching event, or null if none found.
   */
  public CalendarEvent getEvent(String eventId) {
    for (CalendarEvent event : events) {
      if (event.getId().equals(eventId)) {
        return event;
      }
    }
    return null;
  }

  /**
   * Checks if the given time is inside the given day.
   *
   * @param time The time to check.
   * @param day The day where it should fit in. Hours, minutes, seconds, milliseconds are ignored.
   * @return True if the time fits inside the given day, false otherwise.
   */
  private boolean isTimeInDay(Date time, Date day) {
    Calendar begin = new GregorianCalendar();
    begin.setTime(time);
    begin.set(Calendar.HOUR, 0);
    begin.set(Calendar.MINUTE, 0);
    begin.set(Calendar.SECOND, 0);
    begin.set(Calendar.MILLISECOND, 0);

    Calendar end = new GregorianCalendar();
    begin.setTime(time);
    begin.add(Calendar.DATE, 1);
    begin.set(Calendar.HOUR, 0);
    begin.set(Calendar.MINUTE, 0);
    begin.set(Calendar.SECOND, 0);
    begin.set(Calendar.MILLISECOND, 0);

    return !begin.before(day) && !end.after(day);
  }

  public Collection<CalendarTask> getTasks() {
    return tasks;
  }

  public Collection<CalendarEvent> getEvents() {
    return events;
  }

  /**
   * Attaches the given task to this calendar.
   *
   * @param task The task to attach.
   */
  public void attach(CalendarTask task) {
    tasks.add(task);
  }

  /**
   * Attaches the given event to this calendar.
   *
   * @param event The event to attach.
   */
  public void attach(CalendarEvent event) {
    events.add(event);
  }
}
