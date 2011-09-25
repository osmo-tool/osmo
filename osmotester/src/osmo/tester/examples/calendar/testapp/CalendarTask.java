package osmo.tester.examples.calendar.testapp;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Teemu Kanstren
 */
public class CalendarTask {
  private Date when = null;
  private String description = null;
  private final String id;
  private static final AtomicInteger nextId = new AtomicInteger(0);

  public CalendarTask(String uid, Date when, String description) {
    this.when = when;
    this.description = description;
    id = uid+":task:"+nextId.incrementAndGet();
  }

  public String getId() {
    return id;
  }

  public Date getWhen() {
    return when;
  }

  public void setWhen(Date when) {
    this.when = when;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
