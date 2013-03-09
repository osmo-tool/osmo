package osmo.tester.examples.calendar.scripter.offline;

import org.apache.velocity.util.StringUtils;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.testmodel.ModelEvent;
import osmo.tester.examples.calendar.testmodel.ModelState;
import osmo.tester.examples.calendar.testmodel.ModelTask;
import osmo.tester.model.data.Text;
import osmo.tester.scripter.robotframework.RFParameter;
import osmo.tester.scripter.robotframework.RFScripter;

import java.io.BufferedWriter;
import java.io.File;
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
  private RFScripter scripter = new RFScripter(6);
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
  /** The script once created. */
  private String script = null;
  private ModelState state;

  public OfflineScripter(ModelState state, String fileName) {
    this.state = state;
    this.fileName = fileName;
    scripter.setTestLibrary(CalendarLibrary.class.getName());
  }

  /**
   * Get the actual script from the RF scripter. If it has been created before, it is returned. If not, it is first
   * created.
   *
   * @return The complete test script.
   */
  public String getScript() {
    if (script != null) {
      return script;
    }
    return createScript();
  }

  /**
   * Create the actual script from the RF scripter.
   *
   * @return The complete test script.
   */
  public String createScript() {
    //create users with random family name length 3-5 characters
    Text text = new Text(3, 5);
    text.asciiLettersAndNumbersOnly();
    for (String user : state.getUsers()) {
      scripter.addVariable(user, StringUtils.capitalizeFirstLetter(user) + " " + text.next());
    }
    script = scripter.createScript();
    return script;
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
  public void removeTask(String userId, String taskId) {
    RFParameter uid = new RFParameter(userId, true);
    RFParameter rfTaskId = new RFParameter(taskId, true);
    scripter.addStep("Remove Task", uid, rfTaskId);
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
  public void removeEvent(String uid, String eventId) {
    RFParameter uidRef = new RFParameter(uid, true);
    RFParameter rfEventId = new RFParameter(eventId, true);
    scripter.addStep("Remove Event", uidRef, rfEventId);
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
      System.out.println("Wrote script to:" + new File(fileName).getAbsolutePath());
    } catch (IOException e) {
      throw new RuntimeException("Failed to write test script to file:" + fileName, e);
    }
  }
}
