package osmo.tester.examples.calendar.testmodel;

import osmo.common.Randomizer;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.CoverageValue;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestCaseStep;
import osmo.tester.model.data.DataGenerationStrategy;
import osmo.tester.model.data.Text;
import osmo.tester.model.data.ValueRange;
import osmo.tester.model.data.ValueSet;

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
  /** Users with calendars. */
  private ValueSet<User> users = new ValueSet<>();
  /** For creating names for users. */
  private Text names = new Text(4, 7).asciiLettersAndNumbersOnly();
  @Variable
  /** Tasks for all users. */
  private ValueSet<ModelTask> tasks = new ValueSet<>();
  @Variable
  /** Events for all users. */
  private ValueSet<ModelEvent> events = new ValueSet<>();
  @Variable
  /** For creating random duration between 1 seconds to 4 hours. */
  private ValueRange<Integer> duration = new ValueRange<>(1000, 1000 * 60 * 60 * 4);
  @Variable
  /** Used to generate start times between January 2000 and December 2010. */
  private ValueRange<Long> startTime = new ValueRange<>(0, 0);
  private int eventCount = 1;
  private int taskCount = 1;
  private int userCount = -1;
  private Randomizer rand = new Randomizer();

  public ModelState() {
    names.setName("name");
    Calendar start = Calendar.getInstance();
    start.setTime(new Date(0));
    start.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
    Calendar end = Calendar.getInstance();
    end.setTime(new Date(0));
    end.set(2010, Calendar.DECEMBER, 31, 23, 59, 59);
    startTime = new ValueRange<>(start.getTimeInMillis(), end.getTimeInMillis());
  }

  public void setUserCount(int userCount) {
    this.userCount = userCount;
  }

  /**
   * Used to initialize state before a test suite is generated.
   * Could be put in constructor as well but demonstrated user of
   */
  public void init() {
    users.clear();
    if (userCount < 1) {
      userCount = rand.nextInt(1, 10);
    }
    for (int i = 1 ; i <= userCount ; i++) {
//      this.users.add("user" + i);
      this.users.add(new User(names.next()));
    }
  }

  /**
   * Clear state between tests.
   */
  public void reset() {
    tasks.clear();
    events.clear();
  }

  public User randomUser() {
    return users.random();
  }

  public boolean hasTasks() {
    return tasks.size() > 0;
  }

  public ModelTask getAndRemoveRandomTask() {
    ModelTask task = tasks.random();
    tasks.remove(task);
    return task;
  }

  public ModelEvent createEvent(User user, Date start, Date end) {
    String description = "event" + eventCount;
    String location = "location" + eventCount++;
    ModelEvent event = new ModelEvent(user, start, end, description, location);
    events.add(event);
    return event;
  }

  public ModelTask createTask(User user, Date time) {
    String description = "task" + taskCount++;
    ModelTask task = new ModelTask(user, time, description);
    tasks.add(task);
    return task;
  }

  public boolean hasEvents() {
    return events.size() > 0;
  }

  public Collection<User> getUsers() {
    Collection<User> result = new ArrayList<>();
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
    ModelEvent ownerEvent = events.random();
    events.remove(ownerEvent);
    return ownerEvent;
  }

  public ParticipantEvent getAndRemoveParticipantEvent() {
    Collection<ParticipantEvent> participants = getParticipants();
    ParticipantEvent participantEvent = oneOf(participants);
    User participant = participantEvent.getParticipant();
    ModelEvent event = participantEvent.getEvent();
    event.removeParticipant(participant);
    return participantEvent;
  }

  private Collection<ParticipantEvent> getParticipants() {
    List<ModelEvent> options = events.getOptions();
    Collection<ParticipantEvent> participants = new ArrayList<>();
    for (ModelEvent event : options) {
      for (User participant : event.getParticipants()) {
        participants.add(new ParticipantEvent(participant, event));
      }
    }
    return participants;
  }

  public ModelEvent getRandomExistingEvent() {
    return events.random();
  }

  public ModelTask getRandomExistingTask() {
    return tasks.random();
  }

  public boolean hasParticipants() {
    return getParticipants().size() > 0;
  }

  public Date randomStartTime() {
    return new Date(startTime.random());
  }

  public Date randomEndTime(Date start) {
    return new Date(start.getTime() + duration.random());
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

  @CoverageValue
  public String state(TestCaseStep step) {
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
