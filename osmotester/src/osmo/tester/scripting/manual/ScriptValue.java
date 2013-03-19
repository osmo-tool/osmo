package osmo.tester.scripting.manual;

/**
 * A value for a given variable in a test step.
 *
 * @author Teemu Kanstren
 */
public class ScriptValue {
  /** The name of the variable. */
  private final String variable;
  /** The value for the variable. */
  private final String value;

  public ScriptValue(String variable, String value) {
    this.variable = variable;
    this.value = value;
  }

  public String getVariable() {
    return variable;
  }

  public String getValue() {
    return value;
  }
}
