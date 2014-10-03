package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.AfterSuite;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Description;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.model.Requirements;

import java.io.PrintStream;
import java.util.Date;

/**
 * The base test model for the calendar. Includes
 * -add  meeting (no participants)
 * -removing meeting from the organizer (possible impact: remove meeting from all participants if any exist)
 *
 * @author Teemu Kanstren
 */
public class CalendarMeetingModel {
  /** The global model state, shared across test models. */
  private final ModelState state;
  /** The scripter for creating/executing the test cases. */
  private final CalendarScripter scripter;
  private final PrintStream out;
  private final Requirements reqs = new Requirements();

  public CalendarMeetingModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
    this.out = System.out;
  }

  public CalendarMeetingModel(ModelState state, CalendarScripter scripter, PrintStream out) {
    this.state = state;
    this.scripter = scripter;
    this.out = out;
  }
  
  @BeforeSuite
  public void suiteSetup() {
    state.init();
  }

  @Description("General test setup")
  @BeforeTest
  public void setup() {
    scripter.reset();
    state.reset();
    out.println("-NEW TEST");
  }

  @Description("Write script to file")
  @AfterSuite
  public void dump() {
    scripter.write();
  }

  @TestStep("Add Meeting")
  public void addEvent() {
    User user = state.randomUser();
    Date start = state.randomStartTime();
    Date end = state.randomEndTime(start);
    ModelEvent event = state.createEvent(user, start, end);
    out.println("--ADDMEETING:" + event);
    scripter.addEvent(event);
  }

  @Description("Someone has an existing meeting in calendar")
  @Guard("Remove Meeting")
  public boolean guardRemoveOrganizerEvent() {
    return state.hasEvents();
  }

  @TestStep("Remove Meeting")
  public void removeOrganizerEvent() {
    ModelEvent event = state.getAndRemoveOwnerEvent();
    out.println("--REMOVEMEETING:" + event);
    scripter.removeEvent(event.getUser().getId(), event.getEventId());
  }
}
