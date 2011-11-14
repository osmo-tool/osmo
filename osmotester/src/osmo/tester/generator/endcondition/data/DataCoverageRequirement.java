package osmo.tester.generator.endcondition.data;

import osmo.tester.model.dataflow.SearchableInput;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class DataCoverageRequirement {
  private boolean all = false;
  private boolean initialized = true;
  private final String name;
  private Collection<String> values = new ArrayList<String>();

  public DataCoverageRequirement(String name) {
    this.name = name;
  }

  public void addRequirement(Object value) {
    values.add("" + value);
  }

  public void requireAll() {
    all = true;
    initialized = false;
  }

  public boolean isAll() {
    return all;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void initializeFrom(SearchableInput input) {
    Collection options = input.getOptions();
    for (Object option : options) {
      values.add("" + option);
    }
  }

  public Collection<String> getValues() {
    return values;
  }

  public String getName() {
    return name;
  }
}
