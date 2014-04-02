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
    Collection<User> users = state.getUsers();
    Map<User, Collection<ModelTask>> tasks = new HashMap<>();
    Map<User, Collection<ModelEvent>> events = new HashMap<>();
    for (User user : users) {
      tasks.put(user, new LinkedHashSet<>());
      events.put(user, new LinkedHashSet<>());
    }

    for (ModelTask task : state.getTasks()) {
      User user = task.getUser();
      Collection<ModelTask> userTasks = tasks.get(user);
      userTasks.add(task);
    }

    for (ModelEvent event : state.getEvents()) {
      User user = event.getUser();
      Collection<ModelEvent> userEvents = events.get(user);
      userEvents.add(event);
      Collection<User> participants = event.getParticipants();
      for (User participant : participants) {
        Collection<ModelEvent> participantEvents = events.get(participant);
        participantEvents.add(event);
      }
    }

    for (User user : users) {
      scripter.assertUserTasks(user.getId(), tasks.get(user));
      scripter.assertUserEvents(user.getId(), events.get(user));
    }
  }
}
