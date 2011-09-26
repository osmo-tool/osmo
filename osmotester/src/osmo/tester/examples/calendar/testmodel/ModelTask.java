package osmo.tester.examples.calendar.testmodel;

import java.util.Date;

/**
 * @author Teemu Kanstren
 */
public class ModelTask {
  private Date time;
  private String description;
  private String uid;
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
