package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Post;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public class CalendarOracleModel {
  private final ModelState state;
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
