package osmo.tester.examples.calendar.scripter.offline;

/**
 * @author Teemu Kanstren
 */
public class CalculatorLibrary {
  /** Library scope from robot framework. */
  public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";
  /** Library version for robot framework. */
  public static final String ROBOT_LIBRARY_VERSION = "1.0.0";

  /**
   * This library takes no arguments.
   */
  public CalculatorLibrary() {
  }

  public String addTask(String userId, String time, String description) {
    return null;
  }

  public void removeTask(String userId, String taskId) {
  }

  public String addEvent(String userId, String start, String end, String description, String location) {
    return null;
  }

  public void removeEvent(String userId, String eventId) {
  }

  public void assertUserTaskCount(String userId, String expectedCount) {
  }

  public void assertUserHasTask(String userId, String time, String description) {
  }
  
  public void assertUserEventCount(String userId, String expectedCount) {
  }

  public void assertUserHasEvent(String userId, String startTime, String endTime, String description, String location) {
  }

  public void removeNonExistentTask(String userId) {
  }

  public void removeNonExistentEvent(String userId) {
  }

  public void linkEventToUser(String targetUserId, String eventId) {

  }
}
