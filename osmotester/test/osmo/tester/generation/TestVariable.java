package osmo.tester.generation;

import osmo.tester.model.VariableValue;

/** @author Teemu Kanstren */
public class TestVariable implements VariableValue {
  private final String value;

  public TestVariable(String value) {
    this.value = value;
  }

  @Override
  public Object value() {
    return value;
  }
}
