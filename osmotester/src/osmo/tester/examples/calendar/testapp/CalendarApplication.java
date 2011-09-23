package osmo.tester.examples.calendar.testapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * @author Teemu Kanstren
 */
public class CalendarApplication {
  private Collection<CalendarTask> tasks = new ArrayList<CalendarTask>();
  private Collection<CalendarEvent> events = new ArrayList<CalendarEvent>();

  public void addTask(Date date, String description) {

  }

  public void addEvent(Date start, Date end, String description, String location) {

  }

  public CalendarEvent getEventFor(Date time) {
    return null;
  }

  public Collection<CalendarEvent> getEventsFor(Date day) {
    return null;
  }

  public Collection<CalendarTask> getTasksFor(Date day) {
    return null;
  }
}
