package osmo.tester.generator.endcondition.data;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class DataCoverageRequirement {
  private boolean all = false;
  private final String name;
  private Collection<Object> values = new ArrayList<Object>();

  public DataCoverageRequirement(String name) {
    this.name = name;
  }

  public void addRequirement(Object value) {
    values.add(value);
  }

  public void requireAll() {
    all = true;
  }

  public boolean isAll() {
    return all;
  }

  public Collection<Object> getValues() {
    return values;
  }

  public String getName() {
    return name;
  }
}
