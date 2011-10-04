package osmo.tester.scripter.robotframework;

/**
 * Represents a parameter for an action in a Robot Framework test step.
 *
 * @author Teemu Kanstren
 */
public class RFParameter {
  /** The parameter value. */
  private final String value;
  /** If true, the value is actually a reference to a variable name, where the variable contains the value. */
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
