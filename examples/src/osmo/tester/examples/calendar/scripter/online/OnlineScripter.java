package osmo.tester.examples.calendar.scripter.online;

import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.testapp.CalendarApplication;
import osmo.tester.examples.calendar.testapp.CalendarEvent;
import osmo.tester.examples.calendar.testapp.CalendarTask;
import osmo.tester.examples.calendar.testapp.CalendarUser;
import osmo.tester.examples.calendar.testmodel.ModelEvent;
import osmo.tester.examples.calendar.testmodel.ModelTask;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * "Scripter" for online testing, directly invoking the test calendar.
 *
 * @author Teemu Kanstren
 */
public class OnlineScripter implements CalendarScripter {
  /** Maps user identifiers to CalendarUser objects. */
  private Map<String, CalendarUser> users = new LinkedHashMap<>();

  public OnlineScripter() {
  }

  /** Used to reset the scripter between generated tests. */
  @Override
  public void reset() {
    users.clear();
  }

  /**
   * Helper to get the calendar object for the given user.
   *
   * @param uid User whose calendar we want.
   * @return The calendar.
   */
  private CalendarApplication getCalendarFor(String uid) {
    CalendarUser user = users.get(uid);
    if (user == null) {
      user = new CalendarUser();
      users.put(uid, user);
    }
    return user.getCalendar();
  }

  @Override
  public void addTask(ModelTask task) {
    CalendarApplication calendar = getCalendarFor(task.getUid());
    CalendarTask calendarTask = calendar.addTask(task.getTime(), task.getDescription());
    task.setTaskId(calendarTask.getId());
  }

  @Override
  public void removeTask(ModelTask task) {
    CalendarApplication calendar = getCalendarFor(task.getUid());
    calendar.removeTask(task.getTaskId());
  }

  @Override
  public void addEvent(ModelEvent event) {
    CalendarApplication calendar = getCalendarFor(event.getUid());
    CalendarEvent calendarEvent = calendar.addEvent(event.getStart(), event.getEnd(), event.getDescription(), event.getLocation());
    event.setEventId(calendarEvent.getId());
  }

  @Override
  public void removeEvent(String uid, ModelEvent event) {
    CalendarApplication calendar = getCalendarFor(uid);
    calendar.removeEvent(event.getEventId(), false);
  }

  @Override
  public void assertUserTasks(String uid, Collection<ModelTask> tasks) {
    CalendarApplication calendar = getCalendarFor(uid);
    Collection<CalendarTask> calendarTasks = calendar.getTasks();
//    System.out.println("tasks:"+tasks+" ctasks:"+calendarTasks);
    if (tasks == null) {
      if (calendarTasks != null && calendarTasks.size() > 0) {
        fail("Tasks are null in model, should be empty also in calendar");
      }
      return;
    }
    assertEquals("Number of tasks in model vs calendar", tasks.size(), calendarTasks.size());
    for (ModelTask modelTask : tasks) {
      String description = modelTask.getDescription();
      Date time = modelTask.getTime();
      boolean found = false;
      for (CalendarTask calendarTask : calendarTasks) {
        if (!calendarTask.getDescription().equals(description)) {
          continue;
        }
        if (!calendarTask.getWhen().equals(time)) {
          continue;
        }
        found = true;
      }
      assertTrue("ModelTask not found on calendar:" + modelTask, found);
    }
  }

  @Override
  public void assertUserEvents(String uid, Collection<ModelEvent> events) {
    CalendarApplication calendar = getCalendarFor(uid);
    Collection<CalendarEvent> calendarEvents = calendar.getEvents();
//    System.out.println("uid:"+uid+" events:"+events+" cevents:"+calendarEvents);
    if (events == null) {
      if (calendarEvents != null && calendarEvents.size() > 0) {
        fail("Events are null in model, should be empty also in calendar");
      }
      return;
    }
    assertEquals("Number of events in model vs calendar", events.size(), calendarEvents.size());
    for (ModelEvent modelEvent : events) {
      String description = modelEvent.getDescription();
      Date start = modelEvent.getStart();
      Date end = modelEvent.getEnd();
      boolean found = false;
      for (CalendarEvent calendarEvent : calendarEvents) {
        if (!calendarEvent.getDescription().equals(description)) {
          continue;
        }
        if (!calendarEvent.getStart().equals(start)) {
          continue;
        }
        if (!calendarEvent.getEnd().equals(end)) {
          continue;
        }
        found = true;
      }
      assertTrue("ModelEvent not found on calendar:" + modelEvent, found);
    }
  }

  @Override
  public void removeTaskThatDoesNotExist(String uid) {
    CalendarApplication calendar = getCalendarFor(uid);
    try {
      calendar.removeTask("no such task");
      fail("Removing a task that does not exist should fail.");
    } catch (Exception e) {
      //expected
    }
  }

  @Override
  public void removeEventThatDoesNotExist(String uid) {
    CalendarApplication calendar = getCalendarFor(uid);
    try {
      calendar.removeEvent("no such event", false);
      fail("Removing an event that does not exist should fail.");
    } catch (Exception e) {
      //expected
    }
  }

  @Override
  public void linkEventToUser(ModelEvent event, String uid) {
    CalendarApplication calendarTo = getCalendarFor(uid);
    CalendarApplication calendarFrom = getCalendarFor(event.getUid());
    String eventId = event.getEventId();
//    System.out.println("attached event:"+event);
    calendarTo.attach(calendarFrom.getEvent(eventId));
  }

  @Override
  public void write() {
  }
}
