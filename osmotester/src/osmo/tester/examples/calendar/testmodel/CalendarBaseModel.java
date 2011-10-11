package osmo.tester.examples.calendar.testmodel;

import osmo.tester.OSMOTester;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;

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

  /**
   * This is used to execute the calendar example.
   *
   * @param args command line arguments, ignored.
   */
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
//    osmo.addSuiteEndCondition(new Length(2));
    ModelState state = new ModelState();
//    CalendarScripter scripter = new OnlineScripter();
    CalendarScripter scripter = new OfflineScripter("tests.html");
    osmo.addModelObject(new CalendarBaseModel(state, scripter));
    osmo.addModelObject(new CalendarOracleModel(state, scripter));
    osmo.addModelObject(new CalendarTaskModel(state, scripter));
    osmo.addModelObject(new CalendarOverlappingModel(state, scripter));
    osmo.addModelObject(new CalendarParticipantModel(state, scripter));
    osmo.addModelObject(new CalendarErrorModel(state, scripter));
    osmo.generate();
    scripter.write();
  }

  //time limit = 10 years
  //add task, random time (DONE)
  //add event, random time (DONE)
  //add task, overlapping task (DONE)
  //add event, overlapping event (DONE)
  //add event, overlapping task (DONE)
  //remove chosen event (DONE)
  //remove events in timeframe
  //remove chosen task (DONE)
  //remove tasks in timeframe (IGNORE)
  //check tasks are always correct (post) (DONE)
  //check events are always correct (post) (DONE)
  //remove task that does not exist (DONE)
  //remove event that does not exist (DONE)
  //remove events in timeframe where none exist (IGNORE)
  //remove tasks in timeframe where none exist (IGNORE)
  //link task to several users (IGNORE)
  //link event to several users (DONE)
  //remove task from a single user while linked to others (DONE)
  //check tasks for all users (DONE)
  //check events for all users (DONE)
  //check geteventforday in post, also gettaskforday (IGNORE)
  //user boundary values for task remove and add
  //create specific model object for each boundary
  //create more examples of using dataflow objects
  //create example of failing script
  //create example of oracle in transitions
}
