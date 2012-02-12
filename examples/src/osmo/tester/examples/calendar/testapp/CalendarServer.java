package osmo.tester.examples.calendar.testapp;

import java.util.HashMap;
import java.util.Map;

/**
 * The server handles cascading deletes when an organizer deletes a calendar event with other participants.
 *
 * @author Teemu Kanstren
 */
public class CalendarServer {
  /** Self. */
  private static final CalendarServer singleton = new CalendarServer();
  /** Maps user identifiers to calendars. */
  private Map<String, CalendarApplication> calendars = new HashMap<>();

  private CalendarServer() {
  }

  public void addUser(CalendarUser user) {
    calendars.put(user.getUserId(), user.getCalendar());
  }

  public void deleteEvent(String uid, String eventId) {
    for (CalendarApplication calendar : calendars.values()) {
      if (calendar.getUid().equals(uid)) {
        //avoid infinite recursion
        continue;
      }
      calendar.removeEvent(eventId, true);
    }
  }

  public CalendarApplication calendarFor(String uid) {
    return calendars.get(uid);
  }

  public static CalendarServer getServer() {
    return singleton;
  }
}
