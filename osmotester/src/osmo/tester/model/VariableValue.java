package osmo.tester.model;

/**
 * Represents a state variable value for storage for a given model state.
 * If an object implements this interface, the value returned by value() is stored for the state for the
 * object represented by this field.
 * If this is not implemented by the object, the object is taken as such (consider the object itself to
 * become then the return value of the value() method).
 * <p/>
 * The intent of this interface is to provide means to store specific data for more complex objects.
 * For example, if the state is composed of an object that has modifiable dynamic fields (e.g. setX()),
 * the value will seem the same for all steps since they share the same object. On the other hand,
 * if the value() method returns the value of X from inside the object itself, this effectively snapshots
 * the object state at a given time. Can also be relevant to serialize large objects just for the
 * relevant parts of the state etc.
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
