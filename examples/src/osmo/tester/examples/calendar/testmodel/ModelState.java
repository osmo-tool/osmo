package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Variable;
import osmo.tester.model.dataflow.CollectionCount;
import osmo.tester.model.VariableValue;
import osmo.tester.model.dataflow.ToStringValue;
import osmo.tester.model.dataflow.ValueRange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static osmo.common.TestUtils.*;

/**
 * Holds the overall state of the model.
 *
 * @author Teemu Kanstren
 */
public class ModelState {
  /** Test users with calendars. */
  private Collection<String> uids = new ArrayList<String>();
  /** Tasks for each user. */
  private Map<String, List<ModelTask>> userTasks = new LinkedHashMap<String, List<ModelTask>>();
  /** Events for each user. */
  private Map<String, List<ModelEvent>> userEvents = new LinkedHashMap<String, List<ModelEvent>>();
  /** Used to generate unique identifiers for tasks. */
  private AtomicInteger nextTaskId = new AtomicInteger(0);
  @Variable
  private ToStringValue taskCount = new ToStringValue(nextTaskId);
  /** Used to generate unique identifiers for events. */
  private AtomicInteger nextEventId = new AtomicInteger(0);
  @Variable
  private ToStringValue eventCount = new ToStringValue(nextEventId);
  /** Used to generate start times between January 2000 and December 2010. */
  private ValueRange<Long> startTime;
  @Variable
  private VariableValue userCount = new CollectionCount(uids);

  public ModelState() {
    Calendar start = Calendar.getInstance();
    start.setTime(new Date(0));
    start.set(2000, 0, 1, 0, 0, 0);
    Calendar end = Calendar.getInstance();
    end.setTime(new Date(0));
    end.set(2010, 11, 31, 23, 59, 59);
    startTime = new ValueRange<Long>(start.getTimeInMillis(), end.getTimeInMillis());
  }

  /** Used to reset the state between test generation. */
  public void reset() {
    uids.clear();
    userTasks.clear();
    userEvents.clear();
    nextTaskId.set(0);
    nextEventId.set(0);
//    nextTaskId = new AtomicInteger(0);
//    nextEventId = new AtomicInteger(0);

    int users = cInt(1, 5);
    for (int i = 1; i <= users; i++) {
      uids.add("user" + i);
    }
  }

  public String randomUID() {
    return oneOf(uids);
  }

  public boolean hasTasks() {
    return userTasks.size() > 0;
  }

  private String keyFor(Set<String> keys) {
    List<String> items = new ArrayList<String>();
    items.addAll(keys);
    Collections.sort(items);
    return oneOf(items);
  }

  public ModelTask getAndRemoveRandomTask() {
    Set<String> keys = userTasks.keySet();
//    String uid = oneOf(keys);
    String uid = keyFor(keys);
    Collection<ModelTask> tasks = userTasks.get(uid);
    ModelTask task = oneOf(tasks);
    tasks.remove(task);
    if (tasks.size() == 0) {
      userTasks.remove(uid);
    }
    return task;
  }

  public ModelEvent createEvent(String uid, Date start, Date end) {
    int count = nextEventId.incrementAndGet();
    String description = "event" + count;
    String location = "location" + count;
    Collection<ModelEvent> events = getOrCreateEvents(uid);
    ModelEvent event = new ModelEvent(uid, start, end, description, location);
    events.add(event);
    return event;
  }

  private Collection<ModelEvent> getOrCreateEvents(String uid) {
    List<ModelEvent> events = userEvents.get(uid);
    if (events == null) {
//      System.out.println("created:"+uid);
      events = new ArrayList<ModelEvent>();
      userEvents.put(uid, events);
    }
    return events;
  }

  public ModelTask createTask(String uid, Date time) {
    String description = "task" + nextTaskId.incrementAndGet();
    Collection<ModelTask> tasks = getOrCreateTasks(uid);
    ModelTask task = new ModelTask(uid, time, description);
    tasks.add(task);
    return task;
  }

  private Collection<ModelTask> getOrCreateTasks(String uid) {
    List<ModelTask> tasks = userTasks.get(uid);
    if (tasks == null) {
      tasks = new ArrayList<ModelTask>();
      userTasks.put(uid, tasks);
    }
    return tasks;
  }

  public boolean hasEvents() {
    return userEvents.size() > 0;
  }

  public Collection<String> getUids() {
    return uids;
  }

  public Collection<ModelTask> getTasksFor(String uid) {
    return userTasks.get(uid);
  }

  public Collection<ModelEvent> getEventsFor(String uid) {
    return userEvents.get(uid);
  }

  public ModelEvent getAndRemoveOrganizerEvent() {
    Collection<ModelEvent> uniqueEvents = getUniqueEvents();
    ModelEvent event = oneOf(uniqueEvents);
    Collection<String> toRemove = new HashSet<String>();
    for (String uid : userEvents.keySet()) {
      Collection<ModelEvent> events = userEvents.get(uid);
      events.remove(event);
      if (events.size() == 0) {
        toRemove.add(uid);
      }
    }
    for (String uid : toRemove) {
      userEvents.remove(uid);
//      System.out.println("remove:"+uid);
    }
    return event;
  }

  public ParticipantEvent getAndRemoveParticipantEvent() {
    Collection<ParticipantEvent> participantEvents = getParticipantEvents();
    ParticipantEvent participantEvent = oneOf(participantEvents);
    ModelEvent event = participantEvent.getEvent();
    String uid = participantEvent.getParticipant();
    Collection<ModelEvent> events = userEvents.get(uid);
    events.remove(event);
    if (events.size() == 0) {
      userEvents.remove(uid);
//      System.out.println("remove:"+uid);
    }
    return participantEvent;
  }

  public ModelEvent getRandomExistingEvent() {
    Set<String> keys = userEvents.keySet();
//    String uid = oneOf(keys);
    String uid = keyFor(keys);
    Collection<ModelEvent> events = userEvents.get(uid);
    return oneOf(events);
  }

  public ModelTask getRandomExistingTask() {
    Set<String> keys = userTasks.keySet();
//    String uid = oneOf(keys);
    String uid = keyFor(keys);
    Collection<ModelTask> tasks = userTasks.get(uid);
    return oneOf(tasks);
  }

  public void attach(String uid, ModelEvent event) {
    Collection<ModelEvent> events = getOrCreateEvents(uid);
    if (!events.contains(event)) {
      events.add(event);
    }
  }

  /**
   * Get the list of events attached to users where the organizer is not the attached user.
   *
   * @return List of participants for events.
   */
  private Collection<ParticipantEvent> getParticipantEvents() {
    Collection<ParticipantEvent> results = new LinkedHashSet<ParticipantEvent>();
    for (String uid : userEvents.keySet()) {
      Collection<ModelEvent> events = userEvents.get(uid);
      for (ModelEvent event : events) {
        if (!event.getUid().equals(uid)) {
          results.add(new ParticipantEvent(uid, event));
        }
      }
    }
    return results;
  }

  /**
   * Provides a set of unique event, removing duplicates due to participants.
   *
   * @return Set of unique events.
   */
  private Collection<ModelEvent> getUniqueEvents() {
    Collection<ModelEvent> results = new ArrayList<ModelEvent>();
    for (String uid : userEvents.keySet()) {
      List<ModelEvent> events = userEvents.get(uid);
      for (ModelEvent event : events) {
        if (!results.contains(event)) {
          results.add(event);
        }
      }
    }
    return results;
  }

  public boolean hasParticipantEvents() {
    return getParticipantEvents().size() > 0;
  }

  public Date randomStartTime() {
    return new Date(startTime.next());
  }

  @Override
  public String toString() {
    return "ModelState{" +
            "uids=" + uids +
            ", userTasks=" + userTasks +
            ", userEvents=" + userEvents +
            '}';
  }

}
