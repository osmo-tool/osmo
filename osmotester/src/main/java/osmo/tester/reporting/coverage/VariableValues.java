package osmo.tester.reporting.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** 
 * Used internally to represent values for a variable in coverage reports.
 * 
 * @author Teemu Kanstren 
 */
public class VariableValues {
  /** Variable name. */
  private final String name;
  /** The values it had. */
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
