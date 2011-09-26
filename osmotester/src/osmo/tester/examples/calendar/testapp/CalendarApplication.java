package osmo.tester.examples.calendar.testapp;

import osmo.tester.examples.calendar.testmodel.ModelTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

/**
 * @author Teemu Kanstren
 */
public class CalendarApplication {
  private final CalendarServer server = CalendarServer.getServer();
  private Collection<CalendarTask> tasks = new HashSet<CalendarTask>();
  private Collection<CalendarEvent> events = new HashSet<CalendarEvent>();
  private final String uid;

  public CalendarApplication(CalendarUser user) {
    uid = user.getUserId();
  }

  public CalendarTask addTask(Date date, String description) {
    CalendarTask task = new CalendarTask(uid, date, description);
    tasks.add(task);
    return task;
  }

  public CalendarEvent addEvent(Date start, Date end, String description, String location) {
    CalendarEvent event = new CalendarEvent(uid, start, end, description, location);
    events.add(event);
    return event;
  }

  public void removeTask(String taskId) {
    CalendarTask toRemove = getTask(taskId);
    if (toRemove == null) {
      throw new IllegalArgumentException("Task to remove does not exist:"+taskId);
    }
    tasks.remove(toRemove);
  }

  public CalendarTask getTask(String taskId) {
    for (CalendarTask task : tasks) {
      if (task.getId().equals(taskId)) {
        return task;
      }
    }
    return null;
  }

  public void removeEvent(String eventId, boolean serverSource) {
    CalendarEvent toRemove = getEvent(eventId);
    if (toRemove == null) {
      if (!serverSource) {
        throw new IllegalArgumentException("Event to remove does not exist:"+eventId);
      }
      return;
    }
    if (toRemove.getOrganizer().equals(uid)) {
      System.out.println("Server delete:"+toRemove.getOrganizer()+" uid:"+uid);
      server.deleteEvent(uid, eventId);
    }
    events.remove(toRemove);
  }

  public String getUid() {
    return uid;
  }

  public CalendarEvent getEvent(String eventId) {
    for (CalendarEvent event : events) {
      if (event.getId().equals(eventId)) {
        return event;
      }
    }
    return null;
  }


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

  public Collection<CalendarEvent> getEventsFor(Date day) {
    Collection<CalendarEvent> result = new ArrayList<CalendarEvent>();
    for (CalendarEvent event : events) {
      if (isTimeInDay(event.getStart(), day) || isTimeInDay(event.getEnd(), day)) {
        result.add(event);
      }
    }
    return result;
  }

  public Collection<CalendarTask> getTasksFor(Date day) {
    Collection<CalendarTask> result = new ArrayList<CalendarTask>();
    for (CalendarTask task : tasks) {
      if (isTimeInDay(task.getWhen(), day)) {
        result.add(task);
      }
    }
    return result;
  }

  public Collection<CalendarTask> getTasks() {
    return tasks;
  }

  public Collection<CalendarEvent> getEvents() {
    return events;
  }

  public void attach(CalendarTask task) {
    tasks.add(task);
  }

  public void attach(CalendarEvent event) {
    events.add(event);
  }
}
