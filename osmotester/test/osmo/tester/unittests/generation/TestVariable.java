package osmo.tester.unittests.generation;

import osmo.tester.model.VariableValue;

/** @author Teemu Kanstren */
public class TestVariable implements VariableValue {
  private final String value;
  private int counter = 0;

  public TestVariable(String value) {
    this.value = value;
  }

  @Override
  public Object value() {
    return value;
  }

  @Override
  public String toString() {
    counter++;
    return value+" "+counter;
  }
}
