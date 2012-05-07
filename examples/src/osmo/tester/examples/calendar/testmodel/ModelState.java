package osmo.tester.examples.calendar.testmodel;

import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.ValueRange;
import osmo.tester.model.dataflow.ValueSet;

import java.util.*;

import static osmo.common.TestUtils.cInt;
import static osmo.common.TestUtils.oneOf;

/**
 * Holds the overall state of the model.
 *
 * @author Teemu Kanstren
 */
public class ModelState {
  /** Users with calendars. */
  private ValueSet<String> users;
  /** Tasks for each user. */
  private ValueSet<ModelTask> tasks;
  /** Events for each user. */
  private ValueSet<ModelEvent> events;
  /** Used to generate unique identifiers for tasks. */
  private ValueRange<Integer> taskId;
  /** Used to generate unique identifiers for events. */
  private ValueRange<Integer> eventId;
  /** Used to generate start times between January 2000 and December 2010. */
  private ValueRange<Long> startTime;

  public ModelState() {
    Calendar start = Calendar.getInstance();
    start.setTime(new Date(0));
    start.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
    Calendar end = Calendar.getInstance();
    end.setTime(new Date(0));
    end.set(2010, Calendar.DECEMBER, 31, 23, 59, 59);
    startTime = new ValueRange<>(start.getTimeInMillis(), end.getTimeInMillis());
  }

  /** Used to reset the state between test generation. */
  public void reset() {
    users = new ValueSet<>(DataGenerationStrategy.RANDOM);
    taskId = new ValueRange<>(0, Integer.MAX_VALUE);
    eventId = new ValueRange<>(0, Integer.MAX_VALUE);
    taskId.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    eventId.setStrategy(DataGenerationStrategy.ORDERED_LOOP);
    tasks = new ValueSet<>();
    events = new ValueSet<>();

    int users = cInt(1, 5);
    for (int i = 1; i <= users; i++) {
      this.users.add("user" + i);
    }
  }

  public String randomUID() {
    return users.next();
  }

  public boolean hasTasks() {
    return tasks.size() > 0;
  }

  public ModelTask getAndRemoveRandomTask() {
    ModelTask task = tasks.next();
    tasks.remove(task);
    return task;
  }

  public ModelEvent createEvent(String uid, Date start, Date end) {
    int count = eventId.next();
    String description = "event" + count;
    String location = "location" + count;
    ModelEvent event = new ModelEvent(uid, start, end, description, location);
    events.add(event);
    return event;
  }

  public ModelTask createTask(String uid, Date time) {
    String description = "task" + taskId.next();
    ModelTask task = new ModelTask(uid, time, description);
    tasks.add(task);
    return task;
  }

  public boolean hasEvents() {
    return events.size() > 0;
  }

  public Collection<String> getUsers() {
    return users.getOptions();
  }

  public Collection<ModelTask> getTasks() {
    return tasks.getOptions();
  }

  public Collection<ModelEvent> getEvents() {
    return events.getOptions();
  }

  public ModelEvent getAndRemoveOwnerEvent() {
    ModelEvent ownerEvent = events.next();
    List<ModelEvent> options = events.getOptions();
    Collection<ModelEvent> toRemove = new ArrayList<>();
    for (ModelEvent event : options) {
      if (event.getEventId().equals(ownerEvent.getEventId())) {
        toRemove.add(event);
      }
    }
    for (ModelEvent event : toRemove) {
      events.remove(event);
    }
    return ownerEvent;
  }

  public ModelEvent getAndRemoveParticipantEvent() {
    Collection<ModelEvent> participants = getParticipants();
    return oneOf(participants);
  }

  private Collection<ModelEvent> getParticipants() {
    List<ModelEvent> options = events.getOptions();
    Collection<ModelEvent> participants = new ArrayList<>();
    for (ModelEvent event : options) {
      participants.add(event);
    }
    return participants;
  }

  public ModelEvent getRandomExistingEvent() {
    return events.next();
  }

  public ModelTask getRandomExistingTask() {
    return tasks.next();
  }

  public boolean hasParticipants() {
    Collection<ModelEvent> participants = getParticipants();
    return participants.size() > 0;
  }

  public Date randomStartTime() {
    return new Date(startTime.next());
  }

  @Override
  public String toString() {
    return "ModelState{" +
            "uids=" + users +
            '}';
  }

}
