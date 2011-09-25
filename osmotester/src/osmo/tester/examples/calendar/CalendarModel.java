package osmo.tester.examples.calendar;

import osmo.tester.OSMOTester;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.testapp.CalendarEvent;
import osmo.tester.examples.calendar.testapp.CalendarTask;
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
  private Collection<String> uids = new ArrayList<String>();
  private Map<String, Collection<ModelTask>> userTasks = new HashMap<String, Collection<ModelTask>>();
  private Map<String, Collection<ModelEvent>> userEvents = new HashMap<String, Collection<ModelEvent>>();
  private AtomicInteger taskCount = new AtomicInteger(0);
  private AtomicInteger eventCount = new AtomicInteger(0);
  private ValueRange<Long> startTime;

  @BeforeTest
  public void setup() {
    int users = cInt(1, 5);
    for (int i = 0 ; i < users ; i++) {
      uids.add("user"+i);
    }
    scripter = new OnlineScripter();
    Calendar start = Calendar.getInstance();
    start.set(2000, 0, 1);
    Calendar end = Calendar.getInstance();
    end.set(2010, 11, 31);
    startTime = new ValueRange<Long>(start.getTimeInMillis(), end.getTimeInMillis());
    System.out.println("-NEW TEST");
    userTasks.clear();
    userEvents.clear();
  }

  @Transition("AddTask")
  public void addTask() {
    String uid = oneOf(uids);
    String description = "task" + taskCount.incrementAndGet();
    Date time = new Date(startTime.next());
    ModelTask task = new ModelTask(uid, time, description);
    scripter.addTask(task);
    Collection<ModelTask> tasks = userTasks.get(uid);
    if (tasks == null) {
      tasks = new ArrayList<ModelTask>();
      userTasks.put(uid, tasks);
    }
    tasks.add(task);
    System.out.println("--ADDTASK");
  }

  @Guard("RemoveTask")
  public boolean guardRemoveTask() {
    return userTasks.size() > 0;
  }

  @Transition("RemoveTask")
  public void removeTask() {
    Set<String> keys = userTasks.keySet();
    String uid = oneOf(keys);
    Collection<ModelTask> tasks = userTasks.get(uid);
    ModelTask task = oneOf(tasks);
    scripter.removeTask(task);
    tasks.remove(task);
    if (tasks.size() == 0) {
      userTasks.remove(uid);
    }
    System.out.println("--REMOVETASK");
  }

  private ModelEvent createEvent(String uid, Date start, Date end) {
    int count = eventCount.incrementAndGet();
    String description = "event" + count;
    String location = "location" + count;
    return new ModelEvent(uid, start, end, description, location);
  }

  @Transition("AddEvent")
  public void addEvent() {
    String uid = oneOf(uids);
    Date start = new Date(startTime.next());
    Date end = calculateEndTime(start);
    ModelEvent event = createEvent(uid, start, end);
    scripter.addEvent(event);
    Collection<ModelEvent> events = userEvents.get(uid);
    if (events == null) {
      events = new ArrayList<ModelEvent>();
      userEvents.put(uid, events);
    }
    events.add(event);
    System.out.println("--ADDEVENT");
  }

  private Date calculateEndTime(Date start) {
    //1000 = second, 60=minute, 60=hour, 4=four hours max
    long max = 1000 * 60 * 60 * 4;
    //min 1 second, max 4 hours
    return new Date(startTime.next() + cLong(1000, max));
  }

  @Post
  public void genericOracle() {
    for (String uid : uids) {
      Collection<ModelTask> tasks = userTasks.get(uid);
      scripter.assertUserTasks(uid, tasks);
      Collection<ModelEvent> events = userEvents.get(uid);
      scripter.assertUserEvents(uid, events);
    }
  }

  @Guard("RemoveEvent")
  public boolean guardRemoveEvent() {
    return userEvents.size() > 0;
  }

  @Transition("RemoveEvent")
  public void removeEvent() {
    Set<String> keys = userEvents.keySet();
    String uid = oneOf(keys);
    Collection<ModelEvent> events = userEvents.get(uid);
    ModelEvent event = oneOf(events);
    scripter.removeEvent(event);
    events.remove(event);
    if (events.size() == 0) {
      userEvents.remove(uid);
    }
    System.out.println("--REMOVEEVENT");
  }

  @Guard("AddOverlappingEvent")
  public boolean guardAddOverLappingEvent() {
    return userEvents.size() > 0;
  }

  @Transition("AddOverlappingEvent")
  public void addOverlappingEvent() {
    Set<String> keys = userEvents.keySet();
    String uid = oneOf(keys);
    Collection<ModelEvent> events = userEvents.get(uid);
    ModelEvent event = oneOf(events);
    long diff = event.getEnd().getTime() - event.getStart().getTime();
    Date start = new Date(event.getStart().getTime()+cLong(500, diff));
    Date end = calculateEndTime(start);
    ModelEvent overLapping = createEvent(event.getUid(), start, end);
    scripter.addEvent(overLapping);
    events.add(overLapping);
    System.out.println("--ADDOVERLAPPINGEVENT");
  }

  @Guard("AddOverlappingTask")
  public boolean guardAddOverLappingTask() {
    return userTasks.size() > 0;
  }

  @Transition("AddOverlappingTask")
  public void addOverlappingTask() {

  }

  //time limit = 10 years
  //add task, random time (DONE)
  //add event, random time (DONE)
  //add task, overlapping task
  //add event, overlapping event
  //add event, overlapping task
  //remove chosen event (DONE)
  //remove events in timeframe
  //remove chosen task (DONE)
  //remove tasks in timeframe
  //check tasks are always correct (post) (DONE)
  //check events are always correct (post) (DONE)
  //remove task that does not exist
  //remove event that does not exist
  //remove events in timeframe where none exist
  //remove tasks in timeframe where none exist
  //link task to several users
  //link event to several users
  //remove task from a single user while linked to others
  //check tasks for all users (DONE)
  //check events for all users (DONE)

  //user boundary values for task remove and add
  //create specific model object for each boundary
  public static void main(String[] args) {
    OSMOTester osmo = new OSMOTester();
    osmo.addModelObject(new CalendarModel());
    osmo.generate();
  }
}
