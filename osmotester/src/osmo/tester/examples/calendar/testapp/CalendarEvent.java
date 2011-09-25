package osmo.tester.examples.calendar.testapp;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Teemu Kanstren
 */
public class CalendarEvent {
  private Date start = null;
  private Date end = null;
  private String description = null;
  private String location = null;
  private final String id;
  private static final AtomicInteger nextId = new AtomicInteger(0);

  public CalendarEvent(String uid, Date start, Date end, String description, String location) {
    this.start = start;
    this.end = end;
    this.description = description;
    this.location = location;
    id = uid+":event:"+nextId.incrementAndGet();
  }

  public String getId() {
    return id;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  @Override
  public String toString() {
    return "CalendarEvent{" +
            "id='" + id + '\'' +
            '}';
  }
}
