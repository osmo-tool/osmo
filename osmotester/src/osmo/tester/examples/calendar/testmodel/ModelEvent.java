package osmo.tester.examples.calendar.testmodel;

import java.util.Date;

/**
 * Describes a calendar event for the test model.
 *
 * @author Teemu Kanstren
 */
public class ModelEvent { // implements Comparable<ModelEvent> {
  /** Start time for the event. */
  private Date start;
  /** End time for the event. */
  private Date end;
  /** Description of the event. */
  private String description;
  /** Location of the event. */
  private String location;
  /** ID for the user who is the organizer. */
  private String uid;
  /** Unique ID for the event. */
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

  @Override
  public String toString() {
    return "ModelEvent{" +
            "uid='" + uid + '\'' +
            ", description='" + description + '\'' +
            ", location='" + location + '\'' +
            ", start=" + start +
            ", end=" + end +
            '}';
  }

  public int compareTo(ModelEvent o) {
    return toString().hashCode()-o.toString().hashCode();
  }
}
