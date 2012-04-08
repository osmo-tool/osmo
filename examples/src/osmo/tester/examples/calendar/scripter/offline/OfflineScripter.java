package osmo.tester.examples.calendar.scripter.offline;

import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.testmodel.ModelEvent;
import osmo.tester.examples.calendar.testmodel.ModelTask;
import osmo.tester.model.dataflow.Words;
import osmo.tester.scripter.robotframework.RFParameter;
import osmo.tester.scripter.robotframework.Scripter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * Offline scripter for the calendar example. Creates a test script to be executed with the
 * robot framework.
 *
 * @author Teemu Kanstren
 */
public class OfflineScripter implements CalendarScripter {
  /** The OSMO robot framework scripter, script table size 6 columns. */
  private Scripter scripter = new Scripter(6);
  /** Number of currently generated test. */
  private int nextTestId = 1;
  /** Id for next task to create in the script. */
  private int nextTaskId = 1;
  /** Id for next event to create in the script. */
  private int nextEventId = 1;
  /** For formatting dates as text strings in scripts. */
  private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z", Locale.ENGLISH);
  /** Name of the file where the script will be stored. */
  private final String fileName;

  public OfflineScripter(String fileName) {
    this.fileName = fileName;
    scripter.setTestLibrary(CalculatorLibrary.class.getName());
    //create users with random family name length 3-5 characters
    Words words = new Words(3, 5);
    words.asciiLettersAndNumbersOnly();
    scripter.addVariable("user1", "OSMO " + words.next());
    scripter.addVariable("user2", "OSMO " + words.next());
    scripter.addVariable("user3", "OSMO " + words.next());
    scripter.addVariable("user4", "OSMO " + words.next());
    scripter.addVariable("user5", "OSMO " + words.next());
  }

  /**
   * Create the actual script from the RF scripter.
   *
   * @return The complete test script.
   */
  public String getScript() {
    return scripter.createScript();
  }

  @Override
  public void reset() {
    scripter.startTest("Test" + nextTestId);
    nextTestId++;
  }

  /**
   * Helper method to format dates for text scripts.
   *
   * @param time The date to format.
   * @return Date in String format.
   */
  private String formatTime(Date time) {
    return df.format(time);
  }

  @Override
  public void addTask(ModelTask task) {
    String taskId = "Task" + nextTaskId;
    nextTaskId++;
    task.setTaskId(taskId);
    RFParameter start = new RFParameter(formatTime(task.getTime()), false);
    RFParameter description = new RFParameter(task.getDescription(), false);
    RFParameter uid = new RFParameter(task.getUid(), true);
    scripter.addStepWithResult("Add Task", taskId, uid, start, description);
  }

  @Override
  public void removeTask(ModelTask task) {
    RFParameter uid = new RFParameter(task.getUid(), true);
    RFParameter taskId = new RFParameter(task.getTaskId(), true);
    scripter.addStep("Remove Task", uid, taskId);
  }

  @Override
  public void addEvent(ModelEvent event) {
    String eventId = "Event" + nextEventId;
    nextEventId++;
    event.setEventId(eventId);
    RFParameter uid = new RFParameter(event.getUid(), true);
    RFParameter start = new RFParameter(formatTime(event.getStart()), false);
    RFParameter end = new RFParameter(formatTime(event.getEnd()), false);
    RFParameter description = new RFParameter(event.getDescription(), false);
    RFParameter location = new RFParameter(event.getLocation(), false);
    scripter.addStepWithResult("Add Event", eventId, uid, start, end, description, location);
  }

  @Override
  public void removeEvent(String uid, ModelEvent event) {
    RFParameter uidRef = new RFParameter(uid, true);
    RFParameter eventId = new RFParameter(event.getEventId(), true);
    scripter.addStep("Remove Event", uidRef, eventId);
  }

  @Override
  public void assertUserTasks(String uid, Collection<ModelTask> tasks) {
    if (tasks == null) {
      tasks = new ArrayList<>();
    }
    RFParameter uidRef = new RFParameter(uid, true);
    RFParameter expectedSize = new RFParameter("" + tasks.size(), false);
    scripter.addStep("Assert User Task Count is", uidRef, expectedSize);
    for (ModelTask task : tasks) {
      RFParameter time = new RFParameter(formatTime(task.getTime()), false);
      RFParameter description = new RFParameter(task.getDescription(), false);
      uidRef = new RFParameter(uid, true);
      scripter.addStep("Assert User has Task", uidRef, time, description);
    }
  }

  @Override
  public void assertUserEvents(String uid, Collection<ModelEvent> events) {
    if (events == null) {
      events = new ArrayList<>();
    }
    RFParameter uidRef = new RFParameter(uid, true);
    RFParameter expectedCount = new RFParameter("" + events.size(), false);
    scripter.addStep("Assert User Event Count is", uidRef, expectedCount);
    for (ModelEvent event : events) {
      RFParameter start = new RFParameter(formatTime(event.getStart()), false);
      RFParameter end = new RFParameter(formatTime(event.getEnd()), false);
      RFParameter description = new RFParameter(event.getDescription(), false);
      RFParameter location = new RFParameter(event.getLocation(), false);
      scripter.addStep("Assert User has Event", uidRef, start, end, description, location);
    }
  }

  @Override
  public void removeTaskThatDoesNotExist(String uid) {
    RFParameter uidRef = new RFParameter(uid, true);
    scripter.addStep("Remove Nonexistent Task for", uidRef);
  }

  @Override
  public void removeEventThatDoesNotExist(String uid) {
    RFParameter uidRef = new RFParameter(uid, true);
    scripter.addStep("Remove Nonexistent Event for", uidRef);
  }

  @Override
  public void linkEventToUser(ModelEvent event, String targetUid) {
    RFParameter tUidRef = new RFParameter(targetUid, true);
    RFParameter sUidRef = new RFParameter(event.getUid(), true);
    RFParameter eventId = new RFParameter(event.getEventId(), true);
    scripter.addStep("Link Event to User", tUidRef, sUidRef, eventId);
  }

  @Override
  public void write() {
    try {
      Writer output = new BufferedWriter(new FileWriter(fileName));
      output.write(getScript());
      output.close();
    } catch (IOException e) {
      throw new RuntimeException("Failed to write test script to file:" + fileName, e);
    }
  }
}
