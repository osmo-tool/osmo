package osmo.tester.examples.calendar.testapp;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A task has a date but no duration, thus no end time, and has a description but no location.
 *
 * @author Teemu Kanstren
 */
public class CalendarTask {
  /** Date for the task. */
  private Date when = null;
  /** Task description. */
  private String description = null;
  /** Unique identifier for the task. */
  private final String id;
  /** Used to assign new task identifiers. */
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

  @Override
  public String toString() {
    return "CalendarTask{" +
            "description='" + description + '\'' +
            ", when=" + when +
            '}';
  }
}
