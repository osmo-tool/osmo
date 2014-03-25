package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Description;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.io.PrintStream;
import java.util.Date;

import static osmo.common.TestUtils.*;

/**
 * Adds overlapping events and tasks to the test model.
 * Includes
 * -Adding an event that overlaps another event in the same calendar
 * -Adding a task that overlaps another task in the same calendar
 * -Adding a task that overlaps another event in the same calendar
 *
 * @author Teemu Kanstren
 */
public class CalendarOverlappingModel {
  /** The global model state, shared across test models. */
  private final ModelState state;
  /** The scripter for creating/executing the test cases. */
  private final CalendarScripter scripter;
  private final PrintStream out;

  public CalendarOverlappingModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
    this.out = System.out;
  }

  public CalendarOverlappingModel(ModelState state, CalendarScripter scripter, PrintStream out) {
    this.state = state;
    this.scripter = scripter;
    this.out = out;
  }

  @Description("Someone has a valid meeting in their calendar")
  @Guard("Add Overlapping Meeting")
  public boolean guardAddOverLappingEvent() {
    return state.hasEvents();
  }

  @Transition("Add Overlapping Meeting")
  public void addOverlappingEvent() {
    ModelEvent event = state.getRandomExistingEvent();
    long diff = event.getEnd().getTime() - event.getStart().getTime();
    Date start = new Date(event.getStart().getTime() + cLong(500, diff));
    Date end = state.randomEndTime(start);
    ModelEvent overLapping = state.createEvent(event.getUser(), start, end);
    out.println("--ADDOVERLAPPINGEVENT:" + event);
    scripter.addEvent(overLapping);
  }

  @Description("Someone has a valid task in their calendar")
  @Guard("Add Overlapping Task")
  public boolean guardAddOverLappingTask() {
    return state.hasTasks();
  }

  @Transition("Add Overlapping Task")
  public void addOverlappingTask() {
    ModelTask task = state.getRandomExistingTask();
    ModelTask overLapping = state.createTask(task.getUser(), task.getTime());
    out.println("--ADDOVERLAPPINGTASK:" + overLapping);
    scripter.addTask(overLapping);
  }

  @Description("Someone has a valid meeting in their calendar")
  @Guard("AddTaskOverlappingEvent")
  public boolean guardAddTaskOverLappingEvent() {
    return state.hasEvents();
  }

  @Transition("AddTaskOverlappingEvent")
  public void addTaskOverlappingEvent() {
    ModelEvent event = state.getRandomExistingEvent();
    ModelTask overLapping = state.createTask(event.getUser(), event.getStart());
    out.println("--ADDTASKOVERLAPPINGEVENT:" + overLapping);
    scripter.addTask(overLapping);
  }
}
