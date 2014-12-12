package osmo.tester.examples.calendar.testmodel;

import java.util.Date;

/**
 * Describes a calendar task for the test model.
 *
 * @author Teemu Kanstren
 */
public class ModelTask { //implements Comparable<ModelTask> {
  /** Time for the task. */
  private Date time;
  /** Description of the task. */
  private String description;
  /** User whose task it is. */
  private User user;
  /** Unique identifier for the task. */
  private String taskId;

  public ModelTask(User user, Date time, String description) {
    this.time = time;
    this.description = description;
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public Date getTime() {
    return time;
  }

  public String getDescription() {
    return description;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  @Override
  public String toString() {
    return "ModelTask{" +
            "user='" + user.getId() + '\'' +
            ", description='" + description + '\'' +
            ", time=" + time +
            '}';
  }

  public int compareTo(ModelTask o) {
    return toString().hashCode() - o.toString().hashCode();
  }
}
