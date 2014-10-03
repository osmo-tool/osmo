package osmo.tester.examples.calendar.testmodel;

/**
 * Describes an event with a participant other than the organizer.
 *
 * @author Teemu Kanstren
 */
public class ParticipantEvent {
  /** User object for participant. */
  private final User participant;
  private final ModelEvent event;

  public ParticipantEvent(User participant, ModelEvent event) {
    this.participant = participant;
    this.event = event;
  }

  public User getParticipant() {
    return participant;
  }

  public ModelEvent getEvent() {
    return event;
  }

  @Override
  public String toString() {
    return "ParticipantEvent{" +
            "participant='" + participant + '\'' +
            ", event=" + event +
            '}';
  }
}
