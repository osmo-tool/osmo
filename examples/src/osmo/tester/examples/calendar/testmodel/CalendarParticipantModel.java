package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.io.PrintStream;
import java.util.Collection;

import static osmo.common.TestUtils.oneOf;

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

  @Guard("LinkEventToUser")
  public boolean guardLinkEventToUser() {
    return state.getEventsWithSpace().size() > 0;
  }

  @Transition("LinkEventToUser")
  public void linkEventToUser() {
    Collection<String> users = state.getUsers();
    ModelEvent event = state.getEventWithSpace();
    Collection<String> participants = event.getParticipants();
    users.removeAll(participants);
    users.remove(event.getUid());
    String user = oneOf(users);
    out.println("--LINKEVENTTOUSER:" + user + " - " + event);
    event.addParticipant(user);
    scripter.linkEventToUser(event, user);
  }

  @Guard("RemoveParticipantEvent")
  public boolean guardRemoveParticipantEvent() {
    return state.hasParticipants();
  }

  @Transition("RemoveParticipantEvent")
  public void removeParticipantEvent() {
    ParticipantEvent event = state.getAndRemoveParticipantEvent();
    out.println("--REMOVEPARTICIPANTEVENT:" + event);
    scripter.removeEvent(event.getParticipant(), event.getEvent());
  }
}
