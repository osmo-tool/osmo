package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

/**
 * @author Teemu Kanstren
 */
public class CalendarErrorModel {
  private final ModelState state;
  private final CalendarScripter scripter;

  public CalendarErrorModel(ModelState state, CalendarScripter scripter) {
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
