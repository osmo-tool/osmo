package osmo.tester.examples.calendar.testmodel;

import osmo.tester.model.dataflow.ValueRange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
  private Map<String, Collection<ModelTask>> userTasks = new HashMap<String, Collection<ModelTask>>();
  /** Events for each user. */
  private Map<String, Collection<ModelEvent>> userEvents = new HashMap<String, Collection<ModelEvent>>();
  /** Used to generate unique identifiers for tasks. */
  private AtomicInteger taskCount = new AtomicInteger(0);
  /** Used to generate unique identifiers for events. */
  private AtomicInteger eventCount = new AtomicInteger(0);
  /** Used to generate start times between January 2000 and December 2010. */
  private ValueRange<Long> startTime;

  public ModelState() {
  }

  /**
   * Used to reset the state between test generation.
   */
  public void reset() {
    uids.clear();
    userTasks.clear();
    userEvents.clear();
    taskCount = new AtomicInteger(0);
    eventCount = new AtomicInteger(0);

    int users = cInt(1, 5);
    for (int i = 1 ; i <= users ; i++) {
      uids.add("user"+i);
    }
    Calendar start = Calendar.getInstance();
    start.set(2000, 0, 1);
    Calendar end = Calendar.getInstance();
    end.set(2010, 11, 31);
    startTime = new ValueRange<Long>(start.getTimeInMillis(), end.getTimeInMillis());
  }

  public String randomUID() {
    return oneOf(uids);
  }

  public boolean hasTasks() {
    return userTasks.size() > 0;
  }

  public ModelTask getAndRemoveRandomTask() {
    Set<String> keys = userTasks.keySet();
    String uid = oneOf(keys);
    Collection<ModelTask> tasks = userTasks.get(uid);
    ModelTask task = oneOf(tasks);
    tasks.remove(task);
    if (tasks.size() == 0) {
      userTasks.remove(uid);
    }
    return task;
  }

  public ModelEvent createEvent(String uid, Date start, Date end) {
    int count = eventCount.incrementAndGet();
    String description = "event" + count;
    String location = "location" + count;
    Collection<ModelEvent> events = getOrCreateEvents(uid);
    ModelEvent event = new ModelEvent(uid, start, end, description, location);
    events.add(event);
    return event;
  }

  private Collection<ModelEvent> getOrCreateEvents(String uid) {
    Collection<ModelEvent> events = userEvents.get(uid);
    if (events == null) {
      events = new HashSet<ModelEvent>();
      userEvents.put(uid, events);
    }
    return events;
  }

  public ModelTask createTask(String uid, Date time) {
    String description = "task" + taskCount.incrementAndGet();
    Collection<ModelTask> tasks = getOrCreateTasks(uid);
    ModelTask task = new ModelTask(uid, time, description);
    tasks.add(task);
    return task;
  }

  private Collection<ModelTask> getOrCreateTasks(String uid) {
    Collection<ModelTask> tasks = userTasks.get(uid);
    if (tasks == null) {
      tasks = new HashSet<ModelTask>();
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
    }
    return participantEvent;
  }

  public ModelEvent getRandomExistingEvent() {
    Set<String> keys = userEvents.keySet();
    String uid = oneOf(keys);
    Collection<ModelEvent> events = userEvents.get(uid);
    return oneOf(events);
  }

  public ModelTask getRandomExistingTask() {
    Set<String> keys = userTasks.keySet();
    String uid = oneOf(keys);
    Collection<ModelTask> tasks = userTasks.get(uid);
    return oneOf(tasks);
  }

  public void attach(String uid, ModelEvent event) {
    getOrCreateEvents(uid).add(event);
  }

  /**
   * Get the list of events attached to users where the organizer is not the attached user.
   *
   * @return List of participants for events.
   */
  private Collection<ParticipantEvent> getParticipantEvents() {
    Collection<ParticipantEvent> results = new HashSet<ParticipantEvent>();
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
    Collection<ModelEvent> results = new HashSet<ModelEvent>();
    for (String uid : userEvents.keySet()) {
      results.addAll(userEvents.get(uid));
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
