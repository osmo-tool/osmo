package osmo.tester.scripter.robotframework;

/**
 * @author Teemu Kanstren
 */
public class RFParameter {
  private final String value;
  private final boolean reference;

  public RFParameter(String value) {
    this.value = value;
    this.reference = false;
  }

  public RFParameter(String value, boolean reference) {
    this.value = value;
    this.reference = reference;
  }

  @Override
  public String toString() {
    if (reference) {
      return "${"+value+"}";
    }
    return value;
  }
}
