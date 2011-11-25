package osmo.tester.scripting.manual;

/** @author Teemu Kanstren */
public class ScriptValue {
  private final String variable;
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
