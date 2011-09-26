package osmo.tester.examples.calendar.testapp;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Teemu Kanstren
 */
public class CalendarUser {
  private final CalendarServer server = CalendarServer.getServer();
  private final CalendarApplication calendar;
  private final String userId;
  private static final AtomicInteger nextId = new AtomicInteger(0);

  public CalendarUser() {
    userId = "uid"+nextId.incrementAndGet();
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
