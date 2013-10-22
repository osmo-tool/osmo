package osmo.tester.model.data;

/**
 * Interface for input data generation objects.
 *
 * @deprecated Just use the actual objects directly.
 * @author Teemu Kanstren
 */
public interface Input<T> {
  /**
   * Generates an input object according to the rules configured for the implementing object.
   *
   * @return The generated input object.
   * @deprecated Use the random() and other similar methods where available
   */
  public T next();

  /**
   * Set data generation algorithm
   *
   * @param algorithm The new algorithm.
   * @deprecated Will be removed in next version.
   */
  public Input<T> setStrategy(DataGenerationStrategy algorithm);

}
