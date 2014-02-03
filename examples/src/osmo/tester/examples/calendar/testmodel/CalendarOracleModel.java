package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Description;
import osmo.tester.annotation.Post;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

  @Description("Check all model state against SUT")
  @Post("all")
  public void genericOracle() {
    Collection<String> users = state.getUsers();
    Map<String, Collection<ModelTask>> tasks = new HashMap<>();
    Map<String, Collection<ModelEvent>> events = new HashMap<>();
    for (String user : users) {
      tasks.put(user, new LinkedHashSet<>());
      events.put(user, new LinkedHashSet<>());
    }

    for (ModelTask task : state.getTasks()) {
      String uid = task.getUid();
      Collection<ModelTask> userTasks = tasks.get(uid);
      userTasks.add(task);
    }

    for (ModelEvent event : state.getEvents()) {
      String uid = event.getUid();
      Collection<ModelEvent> userEvents = events.get(uid);
      userEvents.add(event);
      Collection<String> participants = event.getParticipants();
      for (String participant : participants) {
        Collection<ModelEvent> participantEvents = events.get(participant);
        participantEvents.add(event);
      }
    }

    for (String user : users) {
      scripter.assertUserTasks(user, tasks.get(user));
      scripter.assertUserEvents(user, events.get(user));
    }
  }
}
