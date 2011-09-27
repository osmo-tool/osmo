package osmo.tester.examples.calendar.testmodel;

/**
 * Describes an event with a participant other than the organizer.
 *
 * @author Teemu Kanstren
 */
public class ParticipantEvent {
  /** User identifier (uid) of the participant. */
  private final String participant;
  private final ModelEvent event;

  public ParticipantEvent(String participant, ModelEvent event) {
    this.participant = participant;
    this.event = event;
  }

  public String getParticipant() {
    return participant;
  }

  public ModelEvent getEvent() {
    return event;
  }
}
