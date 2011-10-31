package osmo.tester.examples.calendar.scripter;

import osmo.tester.examples.calendar.testmodel.ModelEvent;
import osmo.tester.examples.calendar.testmodel.ModelTask;

import java.util.Collection;

/**
 * A common interface for the calendar example scripters. Allows easy demonstration of the properties of
 * both online and offline scripting.
 *
 * @author Teemu Kanstren
 */
public interface CalendarScripter {
  /** This is invoked before a new test is started (to script). */
  void reset();

  /**
   * The scripter should create the given calendar task.
   *
   * @param task The task to be created.
   */
  void addTask(ModelTask task);

  /**
   * The scripter should remove the given calendar task.
   *
   * @param task The task to be removed.
   */
  void removeTask(ModelTask task);

  /**
   * The scripter should create the given calendar event.
   *
   * @param event The event to be created.
   */
  void addEvent(ModelEvent event);

  /**
   * The scripter should remove the given calendar event.
   *
   * @param uid   Identifier for the user from whom the event should be removed.
   * @param event The event to be removed.
   */
  void removeEvent(String uid, ModelEvent event);

  /**
   * The scripter should assert that the users tasks in their calendar match those given.
   *
   * @param uid   The identifier for the user whose tasks are to be asserted.
   * @param tasks The expected set of tasks.
   */
  void assertUserTasks(String uid, Collection<ModelTask> tasks);

  /**
   * The scripter should assert that the users events in their calendar match those given.
   *
   * @param uid    The identifier for the user whose events are to be asserted.
   * @param events The expected set of events.
   */
  void assertUserEvents(String uid, Collection<ModelEvent> events);

  /**
   * The scripter should try to remove a non-existent task for the given user and check that a suitable error is created.
   *
   * @param uid The user for whom to try to remove a non-existent task.
   */
  void removeTaskThatDoesNotExist(String uid);

  /**
   * The scripter should try to remove a non-existent event for the given user and check that a suitable error is created.
   *
   * @param uid The user for whom to try to remove a non-existent event.
   */
  void removeEventThatDoesNotExist(String uid);

  /**
   * The scripter should link the given previously created event to the given new user.
   *
   * @param event The event to be linked.
   * @param uid   The new user to link it to.
   */
  void linkEventToUser(ModelEvent event, String uid);

  /** Write the script(s) to a file one generation is done (only relevant in offline generation). */
  void write();
}
