package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.io.PrintStream;
import java.util.Date;

/**
 * Adds tasks to the calendar. Includes
 * -add task
 * -remove task
 *
 * @author Teemu Kanstren
 */
public class CalendarTaskModel {
  /** The global model state, shared across test models. */
  private final ModelState state;
  /** The scripter for creating/executing the test cases. */
  private final CalendarScripter scripter;
  private final PrintStream out;

  public CalendarTaskModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
    this.out = System.out;
  }

  public CalendarTaskModel(ModelState state, CalendarScripter scripter, PrintStream out) {
    this.state = state;
    this.scripter = scripter;
    this.out = out;
  }

  @TestStep("Add Task")
  public void addTask() {
    String uid = state.randomUID();
    Date time = state.randomStartTime();
    ModelTask task = state.createTask(uid, time);
    out.println("--ADDTASK:" + task);
    scripter.addTask(task);
  }

  @Guard("Remove Task")
  public boolean guardRemoveTask() {
    return state.hasTasks();
  }

  @TestStep("Remove Task")
  public void removeTask() {
    ModelTask task = state.getAndRemoveRandomTask();
    out.println("--REMOVETASK:" + task);
    scripter.removeTask(task.getUid(), task.getTaskId());
  }
}

