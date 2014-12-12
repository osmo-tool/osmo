package osmo.tester.examples.calendar.testapp;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Describes an event in the calendar. An event is considered to be one with an organizer,
 * one or more participants (including the organizer), a description, a location, and a start and end time.
 *
 * @author Teemu Kanstren
 */
public class CalendarEvent {
  /** Start of the event. */
  private Date start = null;
  /** End of the event. */
  private Date end = null;
  /** Description of the event. */
  private String description = null;
  /** Location of the event. */
  private String location = null;
  /** The participants in the event. */
  private Collection<String> participants = new HashSet<>();
  /** Unique identifier for the event. */
  private final String id;
  /** Organizer of the event (their unique user id) */
  private final String organizer;
  /** Maintains the next identifier for created event. */
  private static final AtomicInteger nextId = new AtomicInteger(0);

  public CalendarEvent(String uid, Date start, Date end, String description, String location) {
    this.start = start;
    this.end = end;
    this.description = description;
    this.location = location;
    id = uid + ":event:" + nextId.incrementAndGet();
    participants.add(uid);
    organizer = uid;
  }

  public String getOrganizer() {
    return organizer;
  }

  public Collection<String> getParticipants() {
    return participants;
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

  public void addParticipant(String uid) {
    participants.add(uid);
  }

  @Override
  public String toString() {
    return "CalendarEvent{" +
            "description='" + description + '\'' +
            ", location='" + location + '\'' +
            ", start=" + start +
            ", end=" + end +
            '}';
  }
}
