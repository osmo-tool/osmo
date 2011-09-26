package osmo.tester.examples.calendar.testmodel;

import osmo.tester.OSMOTester;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Transition;
import osmo.tester.model.dataflow.ValueRange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static osmo.common.TestUtils.*;

/**
 * @author Teemu Kanstren
 */
public class CalendarModel {
  private OnlineScripter scripter;
  private ValueRange<Long> startTime;
  private ModelState state = new ModelState();

  @BeforeTest
  public void setup() {
    state = new ModelState();
    scripter = new OnlineScripter();
    Calendar start = Calendar.getInstance();
    start.set(2000, 0, 1);
    Calendar end = Calendar.getInstance();
    end.set(2010, 11, 31);
    startTime = new ValueRange<Long>(start.getTimeInMillis(), end.getTimeInMillis());
    System.out.println("-NEW TEST");
  }

  @Transition("AddTask")
  public void addTask() {
    String uid = state.randomUID();
    Date time = new Date(startTime.next());
    ModelTask task = state.createTask(uid, time);
    System.out.println("--ADDTASK:"+task);
    scripter.addTask(task);
  }

  @Guard("RemoveTask")
  public boolean guardRemoveTask() {
    return state.hasTasks();
  }

  @Transition("RemoveTask")
  public void removeTask() {
    ModelTask task = state.getAndRemoveRandomTask();
    System.out.println("--REMOVETASK:"+task);
    scripter.removeTask(task);
  }


  @Transition("AddEvent")
  public void addEvent() {
    String uid = state.randomUID();
    Date start = new Date(startTime.next());
    Date end = calculateEndTime(start);
    ModelEvent event = state.createEvent(uid, start, end);
    System.out.println("--ADDEVENT:"+event);
    scripter.addEvent(event);
  }

  private Date calculateEndTime(Date start) {
    //1000 = second, 60=minute, 60=hour, 4=four hours max
    long max = 1000 * 60 * 60 * 4;
    //min 1 second, max 4 hours
    return new Date(startTime.next() + cLong(1000, max));
  }

  @Post
  public void genericOracle() {
    for (String uid : state.getUids()) {
      Collection<ModelTask> tasks = state.getTasksFor(uid);
      scripter.assertUserTasks(uid, tasks);
      Collection<ModelEvent> events = state.getEventsFor(uid);
      scripter.assertUserEvents(uid, events);
    }
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

  @Guard("RemoveOrganizerEvent")
  public boolean guardRemoveOrganizerEvent() {
    return state.hasEvents();
  }

  @Transition("RemoveOrganizerEvent")
  public void removeOrganizerEvent() {
    ModelEvent event = state.getAndRemoveOrganizerEvent();
    System.out.println("--REMOVEORGANIZEREVENT:"+event);
    scripter.removeEvent(event.getUid(), event);
  }

  @Guard("AddOverlappingEvent")
  public boolean guardAddOverLappingEvent() {
    return state.hasEvents();
  }

  @Transition("AddOverlappingEvent")
  public void addOverlappingEvent() {
    ModelEvent event = state.getRandomExistingEvent();
    long diff = event.getEnd().getTime() - event.getStart().getTime();
    Date start = new Date(event.getStart().getTime()+cLong(500, diff));
    Date end = calculateEndTime(start);
    ModelEvent overLapping = state.createEvent(event.getUid(), start, end);
    System.out.println("--REMOVEORGANIZEREVENT:"+event);
    scripter.addEvent(overLapping);
  }

  @Guard("AddOverlappingTask")
  public boolean guardAddOverLappingTask() {
    return state.hasTasks();
  }

  @Transition("AddOverlappingTask")
  public void addOverlappingTask() {
    ModelTask task = state.getRandomExistingTask();
    ModelTask overLapping = state.createTask(task.getUid(), task.getTime());
    System.out.println("--ADDOVERLAPPINGTASK:"+overLapping);
    scripter.addTask(overLapping);
  }

  @Guard("AddTaskOverlappingEvent")
  public boolean guardAddTaskOverLappingEvent() {
    return state.hasEvents();
  }

  @Transition("AddTaskOverlappingEvent")
  public void addTaskOverlappingEvent() {
    ModelEvent event = state.getRandomExistingEvent();
    ModelTask overLapping = state.createTask(event.getUid(), event.getStart());
    System.out.println("--ADDTASKOVERLAPPINGEVENT:"+overLapping);
    scripter.addTask(overLapping);
  }

  @Transition("RemoveTaskThatDoesNotExist")
  public void removeTaskThatDoesNotExist() {
    System.out.println("--REMOVETASKTHATDOESNOTEXIST:");
    scripter.removeTaskThatDoesNotExist(state.randomUID());
  }

  @Transition("RemoveEventThatDoesNotExist")
  public void removeEventThatDoesNotExist() {
    System.out.println("--REMOVETASKTHATDOESNOTEXIST:");
    scripter.removeEventThatDoesNotExist(state.randomUID());
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
  //link task to several users
  //link event to several users
  //remove task from a single user while linked to others
  //check tasks for all users (DONE)
  //check events for all users (DONE)
  //check geteventforday in post, also gettaskforday
  //user boundary values for task remove and add
  //create specific model object for each boundary

  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new CalendarModel());
    osmo.generate();
  }
}
