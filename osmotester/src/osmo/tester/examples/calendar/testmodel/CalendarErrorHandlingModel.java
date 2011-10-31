package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

/**
 * Adds error handling test steps to the overall test model.
 * Includes
 * -removing a task that does not exist, and checking proper error handling
 * -removing an event that does not exist, and checking proper error handling
 *
 * @author Teemu Kanstren
 */
public class CalendarErrorHandlingModel {
  /** The global model state, shared across test models. */
  private final ModelState state;
  /** The scripter for creating/executing the test cases. */
  private final CalendarScripter scripter;

  public CalendarErrorHandlingModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
  }

  @Transition("RemoveTaskThatDoesNotExist")
  public void removeTaskThatDoesNotExist() {
    System.out.println("--REMOVETASKTHATDOESNOTEXIST:");
    scripter.removeTaskThatDoesNotExist(state.randomUID());
  }

  @Transition("RemoveEventThatDoesNotExist")
  public void removeEventThatDoesNotExist() {
    System.out.println("--REMOVETASKTHATDOESNOTEXIST:");
    scripter.removeEventThatDoesNotExist(state.randomUID());
  }

}
