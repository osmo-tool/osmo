package osmo.tester.examples.calendar;

import osmo.tester.examples.calendar.testapp.CalendarApplication;
import osmo.tester.examples.calendar.testapp.CalendarEvent;
import osmo.tester.examples.calendar.testapp.CalendarTask;
import osmo.tester.examples.calendar.testapp.CalendarUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;

/**
 * @author Teemu Kanstren
 */
public class OnlineScripter {
  private Map<String, CalendarUser> users = new HashMap<String, CalendarUser>();

  public OnlineScripter() {
  }

  private CalendarUser getUser(String uid) {
    CalendarUser user = users.get(uid);
    if (user == null) {
      user = new CalendarUser();
      users.put(uid, user);
    }
    return user;
  }

  private CalendarApplication getCalendarFor(String uid) {
    CalendarUser user = users.get(uid);
    if (user == null) {
      user = new CalendarUser();
      users.put(uid, user);
    }
    return user.getCalendar();
  }

  public void addTask(ModelTask task) {
    CalendarApplication calendar = getCalendarFor(task.getUid());
    CalendarTask calendarTask = calendar.addTask(task.getTime(), task.getDescription());
    task.setTaskId(calendarTask.getId());
  }

  public void removeTask(ModelTask task) {
    CalendarApplication calendar = getCalendarFor(task.getUid());
    calendar.removeTask(task.getTaskId());
  }


  public void addEvent(ModelEvent event) {
    CalendarApplication calendar = getCalendarFor(event.getUid());
    CalendarEvent calendarEvent = calendar.addEvent(event.getStart(), event.getEnd(), event.getDescription(), event.getLocation());
    event.setEventId(calendarEvent.getId());
  }

  public void removeEvent(ModelEvent event) {
    CalendarApplication calendar = getCalendarFor(event.getUid());
    calendar.removeEvent(event.getEventId());
  }

  public void assertUserTasks(String uid, Collection<ModelTask> tasks) {
    CalendarApplication calendar = getCalendarFor(uid);
    Collection<CalendarTask> calendarTasks = calendar.getTasks();
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
      assertTrue("ModelTask not found on calendar:"+modelTask, found);
    }
  }

  public void assertUserEvents(String uid, Collection<ModelEvent> events) {
    CalendarApplication calendar = getCalendarFor(uid);
    Collection<CalendarEvent> calendarEvents = calendar.getEvents();
    if (events == null) {
      if (calendarEvents != null && calendarEvents.size() > 0) {
        fail("Events are null in model, should be empty also in calendar");
      }
      return;
    }
    assertEquals("Number of tasks in model vs calendar", events.size(), calendarEvents.size());
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
      assertTrue("ModelEvent not found on calendar:"+modelEvent, found);
    }
  }

}
