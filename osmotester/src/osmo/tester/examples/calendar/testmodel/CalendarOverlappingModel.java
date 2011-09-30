package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.util.Date;

import static osmo.common.TestUtils.cLong;
import static osmo.tester.examples.calendar.testmodel.ModelHelper.calculateEndTime;

/**
 * Adds overlapping events and tasks to the test model.
 * Includes
 *  -Adding an event that overlaps another event in the same calendar
 *  -Adding a task that overlaps another task in the same calendar
 *  -Adding a task that overlaps another event in the same calendar
 *
 * @author Teemu Kanstren
 */
public class CalendarOverlappingModel {
  /** The global model state, shared across test models. */
  private final ModelState state;
  /** The scripter for creating/executing the test cases. */
  private final CalendarScripter scripter;

  public CalendarOverlappingModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
  }

  @Guard("AddOverlappingEvent")
  public boolean guardAddOverLappingEvent() {
    return state.hasEvents();
  }

  @Transition("AddOverlappingEvent")
  public void addOverlappingEvent() {
    ModelEvent event = state.getRandomExistingEvent();
    long diff = event.getEnd().getTime() - event.getStart().getTime();
    Date start = new Date(event.getStart().getTime()+cLong(500, diff));
    Date end = calculateEndTime(start);
    ModelEvent overLapping = state.createEvent(event.getUid(), start, end);
    System.out.println("--REMOVEORGANIZEREVENT:"+event);
    scripter.addEvent(overLapping);
  }

  @Guard("AddOverlappingTask")
  public boolean guardAddOverLappingTask() {
    return state.hasTasks();
  }

  @Transition("AddOverlappingTask")
  public void addOverlappingTask() {
    ModelTask task = state.getRandomExistingTask();
    ModelTask overLapping = state.createTask(task.getUid(), task.getTime());
    System.out.println("--ADDOVERLAPPINGTASK:"+overLapping);
    scripter.addTask(overLapping);
  }

  @Guard("AddTaskOverlappingEvent")
  public boolean guardAddTaskOverLappingEvent() {
    return state.hasEvents();
  }

  @Transition("AddTaskOverlappingEvent")
  public void addTaskOverlappingEvent() {
    ModelEvent event = state.getRandomExistingEvent();
    ModelTask overLapping = state.createTask(event.getUid(), event.getStart());
    System.out.println("--ADDTASKOVERLAPPINGEVENT:"+overLapping);
    scripter.addTask(overLapping);
  }
}
