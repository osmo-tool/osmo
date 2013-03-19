package osmo.tester.reporting.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** @author Teemu Kanstren */
public class VariableValues {
  private final String name;
  private final List<String> values = new ArrayList<>();

  public VariableValues(String name, Collection<String> values) {
    this.name = name;
    this.values.addAll(values);
  }

  public String getName() {
    return name;
  }

  public List<String> getValues() {
    return values;
  }
}
