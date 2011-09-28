package osmo.tester.examples.calendar.scripter.offline;

import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.testmodel.ModelEvent;
import osmo.tester.examples.calendar.testmodel.ModelTask;
import osmo.tester.scripter.robotframework.Scripter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * @author Teemu Kanstren
 */
public class OfflineScripter implements CalendarScripter {
  private Scripter scripter = new Scripter(6);
  private int index = 1;
  private int nextTaskId = 1;
  private int nextEventId = 1;
  private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z", Locale.ENGLISH);
  private final String fileName;

  public OfflineScripter(String fileName) {
    this.fileName = fileName;
    scripter.setTestLibrary("CalculatorLibrary");
  }

  public String getScript() {
    return scripter.createScript();
  }

  @Override
  public void reset() {
    scripter.startTest("Test" + index);
    index++;
  }

  private String formatTime(Date time) {
    return df.format(time);
  }

  @Override
  public void addTask(ModelTask task) {
    String taskId = "Task" + nextTaskId;
    nextTaskId++;
    task.setTaskId(taskId);
    scripter.addStepWithResult("Add Task", taskId, task.getUid(), formatTime(task.getTime()), task.getDescription());
  }

  @Override
  public void removeTask(ModelTask task) {
    scripter.addStep("Remove Task", task.getUid(), task.getTaskId());
  }

  @Override
  public void addEvent(ModelEvent event) {
    String eventId = "Event" + nextEventId;
    nextEventId++;
    event.setEventId(eventId);
    String uid = event.getUid();
    String start = formatTime(event.getStart());
    String end = formatTime(event.getEnd());
    String description = event.getDescription();
    String location = event.getLocation();
    scripter.addStepWithResult("Add Event", eventId, uid, start, end, description, location);
  }

  @Override
  public void removeEvent(String uid, ModelEvent event) {
    scripter.addStep("Remove Event", event.getUid(), event.getEventId());
  }

  @Override
  public void assertUserTasks(String uid, Collection<ModelTask> tasks) {
    if (tasks == null) {
      tasks = new ArrayList<ModelTask>();
    }
    scripter.addStep("Assert User Task Count is", uid, "" + tasks.size());
    for (ModelTask task : tasks) {
      String time = formatTime(task.getTime());
      String description = task.getDescription();
      scripter.addStep("Assert User has Task", uid, time, description);
    }
  }

  @Override
  public void assertUserEvents(String uid, Collection<ModelEvent> events) {
    if (events == null) {
      events = new ArrayList<ModelEvent>();
    }
    scripter.addStep("Assert User Event Count is", uid, "" + events.size());
    for (ModelEvent event : events) {
      String start = formatTime(event.getStart());
      String end = formatTime(event.getEnd());
      String description = event.getDescription();
      String location = event.getLocation();
      scripter.addStep("Assert User has Event", uid, start, end, description, location);
    }
  }

  @Override
  public void removeTaskThatDoesNotExist(String uid) {
    scripter.addStep("Remove Nonexistent Task for", uid);
  }

  @Override
  public void removeEventThatDoesNotExist(String uid) {
    scripter.addStep("Remove Nonexistent Event for", uid);
  }

  @Override
  public void linkEventToUser(ModelEvent event, String targetUid) {
    scripter.addStep("Link Event to User", targetUid, event.getUid(), event.getEventId());
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
