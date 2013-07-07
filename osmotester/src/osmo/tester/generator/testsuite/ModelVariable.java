package osmo.tester.generator.testsuite;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents information for a data variable in the test model.
 * Defines the name of the variable and a set of values for the variable.
 * Used both to define target coverage values and also keep a list of the covered values.
 * The name of the variable typically refers to the name used for the variable in the source code
 * but can also be anything the field representation object reports (as set by user).
 *
 * @author Teemu Kanstren
 */
public class ModelVariable {
  /** The name of the variable. */
  private final String name;
  /** The values to cover or covered. Must be list to allow several coverage requirements for single value. */
  private final Collection<Object> values;

  public ModelVariable(String name) {
    this.name = name;
    values = new ArrayList<>();
  }

  /**
   * Adds given value to the list to cover/covered.
   * 
   * @param value The value to add.
   * @param merge If true, no duplicates are added.
   */
  public void addValue(Object value, boolean merge) {
    if (merge && values.contains(value)) {
      return;
    }
    values.add(value);
  }

  public String getName() {
    return name;
  }

  public Collection<Object> getValues() {
    return values;
  }

  /**
   * @return The first item in the values list.
   */
  public Object getValue() {
    return values.iterator().next();
  }

  /**
   * Adds all values from the given variable to this variable.
   *
   * @param testVar The variable from which to add all values from.
   * @param merge True if duplicates should be removed.
   */
  public void addAll(ModelVariable testVar, boolean merge) {
    for (Object value : testVar.values) {
      addValue(value, merge);
    }
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

  /**
   * Creates a new instance, copying object attributes to the clone.
   *
   * @return The value clone.
   */
  public ModelVariable cloneMe() {
    ModelVariable clone = new ModelVariable(name);
    clone.values.addAll(values);
    return clone;
  }

  @Override
  public String toString() {
    return name + "(" + values + ")";
  }
}
