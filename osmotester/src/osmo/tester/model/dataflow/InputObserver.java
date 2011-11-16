package osmo.tester.model.dataflow;

/**
 * Interface to be implemented for all classes that should be notified when a SearchableInput generates data values.
 * NOTE: if the class is not registered to all SearchableInput instances, nothing will happen of course..
 * For the OSMO Tester core elements ({@link ValueRange}, {@link ValueSet}, etc.) this is handled by OSMO during
 * model parsing.
 *
 * @author Teemu Kanstren
 */
public interface InputObserver<T> {
  /**
   * Called on registered observers when a data value is generated.
   *
   * @param variable The name of the variable.
   * @param value    The value generated.
   */
  public void value(String variable, T value);
}
