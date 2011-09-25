package osmo.tester.examples.calendar.testapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Teemu Kanstren
 */
public class CalendarApplication {
  private Collection<CalendarTask> tasks = new ArrayList<CalendarTask>();
  private Collection<CalendarEvent> events = new ArrayList<CalendarEvent>();
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
    CalendarTask toRemove = null;
    for (CalendarTask task : tasks) {
      if (task.getId().equals(taskId)) {
        toRemove = task;
      }
    }
    tasks.remove(toRemove);
  }

  public void removeEvent(String eventId) {
    CalendarEvent toRemove = null;
    for (CalendarEvent event : events) {
      if (event.getId().equals(eventId)) {
        toRemove = event;
      }
    }
    events.remove(toRemove);
  }


  public CalendarEvent getEventFor(Date time) {
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

}
