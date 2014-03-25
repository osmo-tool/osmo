package osmo.tester.examples.calendar.testmodel;

import java.util.ArrayList;
import java.util.Collection;
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
  /** Organizing user. */
  private User user;
  /** Unique ID for the event. */
  private String eventId;
  private Collection<User> participants = new ArrayList<>();

  public ModelEvent(User user, Date start, Date end, String description, String location) {
    this.start = start;
    this.end = end;
    this.description = description;
    this.location = location;
    this.user = user;
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

  public User getUser() {
    return user;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public void addParticipant(User user) {
    participants.add(user);
  }

  public void removeParticipant(User user) {
    participants.remove(user);
  }

  public Collection<User> getParticipants() {
    return participants;
  }

  @Override
  public String toString() {
    return "ModelEvent{" +
            "start=" + start +
            ", end=" + end +
            ", description='" + description + '\'' +
            ", location='" + location + '\'' +
            ", user='" + user.getId() + '\'' +
            ", eventId='" + eventId + '\'' +
            ", participants=" + participants +
            '}';
  }

  public int compareTo(ModelEvent o) {
    return toString().hashCode() - o.toString().hashCode();
  }
}
