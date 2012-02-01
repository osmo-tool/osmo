package osmo.tester.model.dataflow;

/**
 * Interface for input data generation objects.
 *
 * @author Teemu Kanstren
 */
public interface Input<T> {
  /**
   * Generates an input object according to the rules configured for the implementing object.
   *
   * @return The generated input object.
   */
  public T next();

  /**
   * Set data generation algorithm
   *
   * @param algorithm The new algorithm.
   */
  public void setStrategy(DataGenerationStrategy algorithm);

}
