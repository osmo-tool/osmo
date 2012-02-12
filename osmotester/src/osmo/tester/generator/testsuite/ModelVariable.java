package osmo.tester.generator.testsuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Represents information for a data variable in the test model.
 * Defines the name of the variable and a set of values for the variable.
 * Used both to define data coverage values and also to define covered values.
 * The name of the variable typically refers to the name used for the variable in the source code.
 *
 * @author Teemu Kanstren
 */
public class ModelVariable {
  /** The name of the variable. */
  private final String name;
  /** The values to cover or covered. Must be list to allow several coverage requirements for single value. */
  private Collection<Object> values = new ArrayList<>();

  public ModelVariable(String name) {
    this.name = name;
  }

  public void addValue(Object value) {
    values.add(value);
  }

  public String getName() {
    return name;
  }

  public Collection<Object> getValues() {
    return values;
  }

  /**
   * Adds all values from the given variable
   *
   * @param testVar The variable from which to add all values from.
   */
  public void addAll(ModelVariable testVar) {
    values.addAll(testVar.values);
  }

  /**
   * Checks if the given value is contained in this ModelVariable.
   *
   * @param value The value to be checked.
   * @return True if the value is defined for this ModelVariable.
   */
  public boolean contains(Object value) {
    return values.contains(value);
  }

  /** Enables merging, meaning that if a single value is added several times, it will only be present once. */
  public void enableMerging() {
    if (values instanceof HashSet) {
      //we are already merging so skip this
      return;
    }
    Collection<Object> temp = values;
    values = new HashSet<>();
    values.addAll(temp);
  }
}
