package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.util.Date;

import static osmo.tester.examples.calendar.testmodel.ModelHelper.calculateEndTime;

/**
 * The base test model for the calendar. Includes
 * -add organizer event, meaning an event that has no participants
 * -removing event from the organizer (possibly impacting remove from all participants if such exist)
 *
 * @author Teemu Kanstren
 */
public class CalendarBaseModel {
  /** The global model state, shared across test models. */
  private final ModelState state;
  /** The scripter for creating/executing the test cases. */
  private final CalendarScripter scripter;

  public CalendarBaseModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
  }

  @BeforeTest
  public void setup() {
    state.reset();
    scripter.reset();
    System.out.println("-NEW TEST");
  }

  @Transition("AddEvent")
  public void addEvent() {
    String uid = state.randomUID();
    Date start = state.randomStartTime();
    Date end = calculateEndTime(start);
    ModelEvent event = state.createEvent(uid, start, end);
    System.out.println("--ADDEVENT:" + event);
    scripter.addEvent(event);
  }

  @Guard("RemoveOrganizerEvent")
  public boolean guardRemoveOrganizerEvent() {
    return state.hasEvents();
  }

  @Transition("RemoveOrganizerEvent")
  public void removeOrganizerEvent() {
    ModelEvent event = state.getAndRemoveOrganizerEvent();
    System.out.println("--REMOVEORGANIZEREVENT:" + event);
    scripter.removeEvent(event.getUid(), event);
  }
}
