package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.StateName;
import osmo.tester.model.dataflow.DataGenerationStrategy;
import osmo.tester.model.dataflow.Text;
import osmo.tester.model.dataflow.ValueRange;
import osmo.tester.model.dataflow.ValueSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static osmo.common.TestUtils.*;

/**
 * Holds the overall state of the model.
 *
 * @author Teemu Kanstren
 */
public class ModelState {
  /** How many users? */
  private ValueRange<Integer> userCount = new ValueRange<>(1, 10);
  /** Users with calendars. */
  private ValueSet<String> users = new ValueSet<>();
  /** Tasks for all users. */
  private ValueSet<ModelTask> tasks = new ValueSet<>();
  /** Events for all users. */
  private ValueSet<ModelEvent> events = new ValueSet<>();
  /** For creating random duration between 1 seconds to 4 hours. */
  private ValueRange<Integer> duration = new ValueRange<>(1000, 1000*60*60*4);
  /** Used to generate start times between January 2000 and December 2010. */
  private ValueRange<Long> startTime = new ValueRange<>(0, 0);
  private int eventCount = 1;
  private int taskCount = 1;

  public ModelState() {
    users = new ValueSet<>();
    Calendar start = Calendar.getInstance();
    start.setTime(new Date(0));
    start.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
    Calendar end = Calendar.getInstance();
    end.setTime(new Date(0));
    end.set(2010, Calendar.DECEMBER, 31, 23, 59, 59);
    startTime = new ValueRange<>(start.getTimeInMillis(), end.getTimeInMillis());

    userCount.setStrategy(DataGenerationStrategy.RANDOM);
    users = new ValueSet<>(DataGenerationStrategy.RANDOM);
    int n = userCount.next();
    Text names = new Text(4,7);
    names.setName("name");
    for (int i = 1; i <= n; i++) {
//      this.users.add("user" + i);
      this.users.add(names.next());
    }
  }

  /** Used to reset the state between test generation. */
  public void reset() {
    tasks = new ValueSet<>();
    events = new ValueSet<>();
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
    String description = "event" + eventCount;
    String location = "location" + eventCount++;
    ModelEvent event = new ModelEvent(uid, start, end, description, location);
    events.add(event);
    return event;
  }

  public ModelTask createTask(String uid, Date time) {
    String description = "task" + taskCount++;
    ModelTask task = new ModelTask(uid, time, description);
    tasks.add(task);
    return task;
  }

  public boolean hasEvents() {
    return events.size() > 0;
  }

  public Collection<String> getUsers() {
    Collection<String> result = new ArrayList<>();
    result.addAll(users.getOptions());
    return result;
  }

  public Collection<ModelTask> getTasks() {
    return tasks.getOptions();
  }

  public Collection<ModelEvent> getEvents() {
    return events.getOptions();
  }

  public ModelEvent getAndRemoveOwnerEvent() {
    ModelEvent ownerEvent = events.next();
    events.remove(ownerEvent);
    return ownerEvent;
  }

  public ParticipantEvent getAndRemoveParticipantEvent() {
    Collection<ParticipantEvent> participants = getParticipants();
    ParticipantEvent participantEvent = oneOf(participants);
    String participant = participantEvent.getParticipant();
    ModelEvent event = participantEvent.getEvent();
    event.removeParticipant(participant);
    return participantEvent;
  }

  private Collection<ParticipantEvent> getParticipants() {
    List<ModelEvent> options = events.getOptions();
    Collection<ParticipantEvent> participants = new ArrayList<>();
    for (ModelEvent event : options) {
      for (String participant : event.getParticipants()) {
        participants.add(new ParticipantEvent(participant, event));
      }
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
    return getParticipants().size() > 0;
  }

  public Date randomStartTime() {
    return new Date(startTime.next());
  }

  public Date randomEndTime(Date start) {
    return new Date(start.getTime()+duration.next());
  }

  @Override
  public String toString() {
    return "ModelState{" +
            "uids=" + users +
            '}';
  }

  public Collection<ModelEvent> getEventsWithSpace() {
    int max = getUsers().size() - 1;
    Collection<ModelEvent> result = new ArrayList<>();
    for (ModelEvent event : events.getOptions()) {
      if (event.getParticipants().size() < max) {
        result.add(event);
      }
    }
    return result;
  }

  public ModelEvent getEventWithSpace() {
    return oneOf(getEventsWithSpace());
  }
  
  @StateName
  public String state() {
    if (hasEvents() && hasTasks()) {
      return "events & tasks";
    }
    if (hasEvents()) {
      return "events";
    }
    if (hasTasks()) {
      return "tasks";
    }
    return "empty";
  }
}
