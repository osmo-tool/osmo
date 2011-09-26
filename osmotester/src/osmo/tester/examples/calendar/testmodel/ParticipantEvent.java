package osmo.tester.examples.calendar.testmodel;

/**
 * @author Teemu Kanstren
 */
public class ParticipantEvent {
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
