package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

/**
 * @author Teemu Kanstren
 */
public class CalendarParticipantModel {
  private final ModelState state;
  private final CalendarScripter scripter;

  public CalendarParticipantModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
  }

  @Guard("LinkEventToUser")
  public boolean guardLinkEventToUser() {
    return state.hasEvents();
  }

  @Transition("LinkEventToUser")
  public void linkEventToUser() {
    ModelEvent event = state.getRandomExistingEvent();
    String uid = state.randomUID();
    System.out.println("--LINKTASKTOUSER:"+uid+" - "+event);
    state.attach(uid, event);
    scripter.linkEventToUser(event, uid);
  }

  @Guard("RemoveParticipantEvent")
  public boolean guardRemoveParticipantEvent() {
    return state.hasParticipantEvents();
  }

  @Transition("RemoveParticipantEvent")
  public void removeParticipantEvent() {
    ParticipantEvent event = state.getAndRemoveParticipantEvent();
    System.out.println("--REMOVEPARTICIPANTEVENT:"+event);
    scripter.removeEvent(event.getParticipant(), event.getEvent());
  }
}
