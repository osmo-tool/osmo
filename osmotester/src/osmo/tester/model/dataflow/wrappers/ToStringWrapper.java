package osmo.tester.model.dataflow.wrappers;

import osmo.tester.model.dataflow.SearchableInput;

/** @author Teemu Kanstren */
public class ToStringWrapper {
  private final SearchableInput input;

  public ToStringWrapper(SearchableInput input) {
    this.input = input;
  }

  @Override
  public String toString() {
    return "" + input.next();
  }
}
