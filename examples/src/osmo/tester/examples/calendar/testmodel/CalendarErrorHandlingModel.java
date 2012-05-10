package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.io.PrintStream;

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
  private final PrintStream out;

  public CalendarErrorHandlingModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
    this.out = System.out;
  }

  public CalendarErrorHandlingModel(ModelState state, CalendarScripter scripter, PrintStream out) {
    this.state = state;
    this.scripter = scripter;
    this.out = out;
  }

  @TestStep("Remove Task That Does Not Exist")
  public void removeTaskThatDoesNotExist() {
    out.println("--REMOVETASKTHATDOESNOTEXIST:");
    scripter.removeTaskThatDoesNotExist(state.randomUID());
  }

  @TestStep("Remove Event That Does Not Exist")
  public void removeEventThatDoesNotExist() {
    out.println("--REMOVETASKTHATDOESNOTEXIST:");
    scripter.removeEventThatDoesNotExist(state.randomUID());
  }

}
