package osmo.tester.examples.calendar.scripter;

import osmo.tester.examples.calendar.testmodel.ModelEvent;
import osmo.tester.examples.calendar.testmodel.ModelTask;

import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public interface CalendarScripter {
  void reset();

  void addTask(ModelTask task);

  void removeTask(ModelTask task);

  void addEvent(ModelEvent event);

  void removeEvent(String uid, ModelEvent event);

  void assertUserTasks(String uid, Collection<ModelTask> tasks);

  void assertUserEvents(String uid, Collection<ModelEvent> events);

  void removeTaskThatDoesNotExist(String uid);

  void removeEventThatDoesNotExist(String uid);

  void linkEventToUser(ModelEvent event, String uid);
}
