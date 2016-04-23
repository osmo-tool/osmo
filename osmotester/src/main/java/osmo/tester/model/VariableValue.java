package osmo.tester.model;

/**
 * Represents a variable value considered of interest to store for coverage calculations.
 * If a field in a model objects is annotated as {@link osmo.tester.annotation.Variable} and
 * implements this interface,  the value returned by value() method here is stored for each step.
 * If this is not implemented by the object, the field content is stored as such instead.
 *
 * @author Teemu Kanstren
 */
public interface VariableValue<T> {
  /**
   * Implement this to return the value that should be stored for this object.
   *
   * @return The value to be stored.
   */
  public T value();
}
