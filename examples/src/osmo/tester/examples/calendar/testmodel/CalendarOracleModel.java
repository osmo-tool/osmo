package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Post;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Adds generic test oracles for checking the user tasks and events after each and every test step.
 * Includes
 * -checks that user (calendar) has the correct number and set of tasks
 * -checks that user (calendar) has the correct number and set of events
 *
 * @author Teemu Kanstren
 */
public class CalendarOracleModel {
  /** The global model state, shared across test models. */
  private final ModelState state;
  /** The scripter for creating/executing the test cases. */
  private final CalendarScripter scripter;
  private final PrintStream out;

  public CalendarOracleModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
    this.out = System.out;
  }

  public CalendarOracleModel(ModelState state, CalendarScripter scripter, PrintStream out) {
    this.state = state;
    this.scripter = scripter;
    this.out = out;
  }

  @Post
  public void genericOracle() {
    Map<String, Collection<ModelTask>> tasks = new HashMap<>();
    for (ModelTask task : state.getTasks()) {
      String uid = task.getUid();
      Collection<ModelTask> userTasks = tasks.get(uid);
      if (userTasks == null) {
        userTasks = new ArrayList<>();
        tasks.put(uid, userTasks);
      }
      userTasks.add(task);
    }

    Map<String, Collection<ModelEvent>> events = new HashMap<>();
    for (ModelEvent event : state.getEvents()) {
      String uid = event.getUid();
      Collection<ModelEvent> userEvents = events.get(uid);
      if (userEvents == null) {
        userEvents = new ArrayList<>();
        events.put(uid, userEvents);
      }
      userEvents.add(event);
      Collection<String> participants = event.getParticipants();
      for (String participant : participants) {

      }
    }
    scripter.assertUserTasks(uid, tasks);
    scripter.assertUserEvents(uid, events);
  }
}
