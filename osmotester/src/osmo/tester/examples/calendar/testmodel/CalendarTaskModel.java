package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.util.Date;

/**
 * @author Teemu Kanstren
 */
public class CalendarTaskModel {
  private final ModelState state;
  private final CalendarScripter scripter;

  public CalendarTaskModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
  }


  @Transition("AddTask")
  public void addTask() {
    String uid = state.randomUID();
    Date time = state.randomStartTime();
    ModelTask task = state.createTask(uid, time);
    System.out.println("--ADDTASK:"+task);
    scripter.addTask(task);
  }

  @Guard("RemoveTask")
  public boolean guardRemoveTask() {
    return state.hasTasks();
  }

  @Transition("RemoveTask")
  public void removeTask() {
    ModelTask task = state.getAndRemoveRandomTask();
    System.out.println("--REMOVETASK:"+task);
    scripter.removeTask(task);
  }
}

