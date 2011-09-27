package osmo.tester.examples.calendar.testmodel;

import java.util.Date;

/**
 * Describes a calendar task for the test model.
 *
 * @author Teemu Kanstren
 */
public class ModelTask {
  /** Time for the task. */
  private Date time;
  /** Description of the task. */
  private String description;
  /** User whose task it is. */
  private String uid;
  /** Unique identifier for the task. */
  private String taskId;

  public ModelTask(String uid, Date time, String description) {
    this.time = time;
    this.description = description;
    this.uid = uid;
  }

  public String getUid() {
    return uid;
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
            "uid='" + uid + '\'' +
            ", description='" + description + '\'' +
            ", time=" + time +
            '}';
  }
}
