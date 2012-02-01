package osmo.tester.examples.calendar.scripter;

import osmo.tester.examples.calendar.testmodel.ModelEvent;
import osmo.tester.examples.calendar.testmodel.ModelTask;

import java.util.Collection;

/** @author Teemu Kanstren */
public class MockScripter implements CalendarScripter {
  @Override
  public void reset() {
  }

  @Override
  public void addTask(ModelTask task) {
  }

  @Override
  public void removeTask(ModelTask task) {
  }

  @Override
  public void addEvent(ModelEvent event) {
  }

  @Override
  public void removeEvent(String uid, ModelEvent event) {
  }

  @Override
  public void assertUserTasks(String uid, Collection<ModelTask> tasks) {
  }

  @Override
  public void assertUserEvents(String uid, Collection<ModelEvent> events) {
  }

  @Override
  public void removeTaskThatDoesNotExist(String uid) {
  }

  @Override
  public void removeEventThatDoesNotExist(String uid) {
  }

  @Override
  public void linkEventToUser(ModelEvent event, String uid) {
  }

  @Override
  public void write() {
  }
}
