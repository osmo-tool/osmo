package osmo.tester.model.dataflow;

/**
 * Interface for output evaluation objects.
 *
 * @author Teemu Kanstren
 */
public interface Output<T> {
  /**
   * Evaluates if the given item fits in the rules represented by the implementing object.
   *
   * @param item This is to be evaluated.
   * @return True if matches the rules.
   */
  public boolean evaluate(T item);

  public boolean evaluateSerialized(String item);
}
