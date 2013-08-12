package osmo.tester.explorer.testmodels;

import osmo.tester.annotation.TestStep;
import osmo.tester.model.data.ValueSet;

/** @author Teemu Kanstren */
public class ValueModel {
  private ValueSet<String> names = new ValueSet<>("Bob", "Alice", "John");
  
  @TestStep("Hello")
  public void hello() {
    names.next();
  }
}
