package osmo.tester.generator.endcondition.data;

import osmo.tester.model.dataflow.SearchableInput;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Defines coverage requirements for {@link DataCoverage} end condition for a given variable.
 * Each variable should have one or more instances of this object, with one or more values
 * defines as required. Some OSMO objects such as {@link osmo.tester.model.dataflow.ValueRange} and
 *
 * @author Teemu Kanstren
 * @{link ValueSet} can also support a definition of requiring all possible values covered.
 */
public class DataCoverageRequirement {
  /** Do we require all possible values? */
  private boolean all = false;
  /** Is any value enough? */
  private boolean any = false;
  /** Did we already initialize this requirement? */
  private boolean initialized = true;
  /** Name of the variable for which the value requirements are defined. */
  private final String name;
  /** The values that need to be covered. */
  private Collection<String> values = new ArrayList<String>();

  /** @param name The name of the variable for which this requirement is defined. */
  public DataCoverageRequirement(String name) {
    this.name = name;
  }

  /**
   * Add a new value that needs to be covered for the variable of this requirement.
   *
   * @param value The value that needs to be covered.
   */
  public void addRequirement(Object value) {
    values.add("" + value);
  }

  /** Defines that all possible values or the variable need to be covered. */
  public void requireAll() {
    all = true;
    any = false;
    initialized = false;
  }

  /** @return True if all values are required. */
  public boolean isAll() {
    return all;
  }

  /** Allows any observed value to satisfy this requirement. */
  public void requireAny() {
    all = false;
    any = true;
  }

  /** @return True if any value is enough to satisfy this requirement. */
  public boolean isAny() {
    return any;
  }

  /** @return True if the requirement is already initialized. */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Set the required values to be covered from the given input parameter.
   *
   * @param input Where to initialize value requirements from.
   */
  public void initializeFrom(SearchableInput input) {
    Collection options = input.getOptions();
    for (Object option : options) {
      values.add("" + option);
    }
  }

  /** @return The required values. */
  public Collection<String> getValues() {
    return values;
  }

  /** @return The name of the variable for which the requirement is defined. */
  public String getName() {
    return name;
  }
}
