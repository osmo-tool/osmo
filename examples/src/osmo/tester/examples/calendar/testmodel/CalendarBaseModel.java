package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.io.PrintStream;
import java.util.Date;

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
  private final PrintStream out;

  public CalendarBaseModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
    this.out = System.out;
  }

  public CalendarBaseModel(ModelState state, CalendarScripter scripter, PrintStream out) {
    this.state = state;
    this.scripter = scripter;
    this.out = out;
  }

  @BeforeTest
  public void setup() {
    state.reset();
    scripter.reset();
    out.println("-NEW TEST");
  }

  @AfterSuite
  public void dump() {
    scripter.write();
  }

  @Transition("Add Meeting")
  public void addEvent() {
    String uid = state.randomUID();
    Date start = state.randomStartTime();
    Date end = state.randomEndTime(start);
    ModelEvent event = state.createEvent(uid, start, end);
    out.println("--ADDMEETING:" + event);
    scripter.addEvent(event);
  }

  @Guard("Remove Meeting")
  public boolean guardRemoveOrganizerEvent() {
    return state.hasEvents();
  }

  @TestStep("Remove Meeting")
  public void removeOrganizerEvent() {
    ModelEvent event = state.getAndRemoveOwnerEvent();
    out.println("--REMOVEMEETING:" + event);
    scripter.removeEvent(event.getUid(), event.getEventId());
  }
}
