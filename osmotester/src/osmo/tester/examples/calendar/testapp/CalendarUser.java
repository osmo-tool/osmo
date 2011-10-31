package osmo.tester.examples.calendar.testapp;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a person who has a calendar. One person can only have one calendar in this implementation.
 *
 * @author Teemu Kanstren
 */
public class CalendarUser {
  /** The server managing connections between calendars. */
  private final CalendarServer server = CalendarServer.getServer();
  /** The calendar object for the user. */
  private final CalendarApplication calendar;
  /** Unique identifier for this user. */
  private final String userId;
  /** Used to assign new user identifiers. */
  private static final AtomicInteger nextId = new AtomicInteger(0);

  public CalendarUser() {
    userId = "uid" + nextId.incrementAndGet();
    calendar = new CalendarApplication(this);
    server.addUser(this);
  }

  public CalendarApplication getCalendar() {
    return calendar;
  }

  public String getUserId() {
    return userId;
  }
}
