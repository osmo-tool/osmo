package osmo.tester.examples.calendar;

import osmo.tester.annotation.Transition;
import osmo.tester.examples.calendar.testapp.CalendarEvent;
import osmo.tester.examples.calendar.testapp.CalendarTask;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Teemu Kanstren
 */
public class CalendarModel {

  @Transition("AddTask")
  public void addTask() {
    
  }

  @Transition("RemoveTask")
  public void removeTask() {

  }

  @Transition("AddEvent")
  public void addEvent() {

  }

  @Transition("RemoveEvent")
  public void removeEvent() {
    
  }

  @Transition("AddOverlappingEvent")
  public void addOverlappingEvent() {

  }

  @Transition("AddOverlappingTask")
  public void addOverlappingTask() {

  }
}
