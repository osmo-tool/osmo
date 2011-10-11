package osmo.tester.examples.calendar.testmodel;

import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.scripter.CalendarScripter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Teemu Kanstren
 */
public class CalendarFailureModel {
  /** The global model state, shared across test models. */
  private final ModelState state;
  /** The scripter for creating/executing the test cases. */
  private final CalendarScripter scripter;

  public CalendarFailureModel(ModelState state, CalendarScripter scripter) {
    this.state = state;
    this.scripter = scripter;
  }

  @Guard("Failing Assertion")
  public boolean shallWeAllowIt() {
    return true;
  }

  @Transition("Failing Assertion")
  public void giefFailure() {
    Collection<ModelEvent> events = new ArrayList<ModelEvent>();
    events.add(new ModelEvent("persikka", new Date(0), new Date(0), null, null));
    scripter.assertUserEvents("bob", events);
    System.out.println("--MUSTIKKA");
  }
}
