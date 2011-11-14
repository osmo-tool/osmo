package osmo.tester.model;

import java.util.Collection;

/** @author Teemu Kanstren */
public class CollectionCount implements VariableValue {
  private final Collection counted;

  public CollectionCount(Collection counted) {
    this.counted = counted;
  }

  @Override
  public Object value() {
    return counted.size();
  }
}
