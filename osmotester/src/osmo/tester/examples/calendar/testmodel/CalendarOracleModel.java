package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Post;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.util.Collection;

/**
 * Adds generic test oracles for checking the user tasks and events after each and every test step.
 * Includes
 *  -checks that user (calendar) has the correct number and set of tasks
 *  -checks that user (calendar) has the correct number and set of events
 *
 * @author Teemu Kanstren
 */
public class CalendarOracleModel {
  /** The global model state, shared across test models. */
  private final ModelState state;
  /** The scripter for creating/executing the test cases. */
  private final CalendarScripter scripter;

  public CalendarOracleModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
  }

  @Post
  public void genericOracle() {
    for (String uid : state.getUids()) {
      Collection<ModelTask> tasks = state.getTasksFor(uid);
      scripter.assertUserTasks(uid, tasks);
      Collection<ModelEvent> events = state.getEventsFor(uid);
      scripter.assertUserEvents(uid, events);
    }
  }
}
