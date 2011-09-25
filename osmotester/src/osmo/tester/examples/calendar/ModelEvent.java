package osmo.tester.examples.calendar;

import java.util.Date;

/**
 * @author Teemu Kanstren
 */
public class ModelEvent {
  private Date start;
  private Date end;
  private String description;
  private String location;
  private String uid;
  private String eventId;

  public ModelEvent(String uid, Date start, Date end, String description, String location) {
    this.start = start;
    this.end = end;
    this.description = description;
    this.location = location;
    this.uid = uid;
  }

  public Date getStart() {
    return start;
  }

  public Date getEnd() {
    return end;
  }

  public String getDescription() {
    return description;
  }

  public String getLocation() {
    return location;
  }

  public String getUid() {
    return uid;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }
}
