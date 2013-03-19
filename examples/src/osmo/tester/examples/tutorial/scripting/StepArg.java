package osmo.tester.examples.tutorial.scripting;

/** @author Teemu Kanstren */
public class StepArg {
  private final String name;
  private final String value;

  public StepArg(String name, String value) {
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
