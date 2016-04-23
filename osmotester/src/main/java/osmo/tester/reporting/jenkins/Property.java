package osmo.tester.reporting.jenkins;

/**
 * Represents a property for the Jenkins report, such as OS version.
 *
 * @author Teemu Kanstren
 */
public class Property {
  /** Property name. */
  private final String name;
  /** Property value. */
  private final String value;

  public Property(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }
}
