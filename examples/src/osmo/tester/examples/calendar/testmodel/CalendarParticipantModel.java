package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Description;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.model.data.ValueSet;

import java.io.PrintStream;
import java.util.Collection;

/**
 * Adds participants to the calendar test model.
 * Includes
 * -Linking an existing event to another user's calendar, effectively making the "another user" a participant in the event
 * -Removing a participant event, removing an event from someone who is a "participant", that is whose user id is not the same as the organizer user id for the event
 *
 * @author Teemu Kanstren
 */
public class CalendarParticipantModel {
  /** The global model state, shared across test models. */
  private final ModelState state;
  /** The scripter for creating/executing the test cases. */
  private final CalendarScripter scripter;
  private final PrintStream out;
  /** Used here to allow scripting to capture and control the data. */
  private ValueSet<String> userId = new ValueSet<>();

  public CalendarParticipantModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
    this.out = System.out;
  }

  public CalendarParticipantModel(ModelState state, CalendarScripter scripter, PrintStream out) {
    this.state = state;
    this.scripter = scripter;
    this.out = out;
  }

  @Description("There is a meeting with room for a new person")
  @Guard("Add Participant")
  public boolean guardLinkEventToUser() {
    return state.getEventsWithSpace().size() > 0;
  }

  @TestStep("Add Participant")
  public void linkEventToUser() {
    Collection<String> users = state.getUsers();
    ModelEvent event = state.getEventWithSpace();
    Collection<String> participants = event.getParticipants();
    users.removeAll(participants);
    users.remove(event.getUid());
    userId.setOptions(users);
    String user = userId.next();
    out.println("--ADDPARTICIPANT:" + user + " - " + event);
    event.addParticipant(user);
    scripter.linkEventToUser(event, user);
  }

  @Description("Some meeting has a participant")
  @Guard("Remove Participant")
  public boolean guardRemoveParticipantEvent() {
    return state.hasParticipants();
  }

  @TestStep("Remove Participant")
  public void removeParticipantEvent() {
    ParticipantEvent event = state.getAndRemoveParticipantEvent();
    out.println("--REMOVEPARTICIPANT:" + event);
    scripter.removeEvent(event.getParticipant(), event.getEvent().getEventId());
  }
}
