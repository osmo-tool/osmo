package osmo.tester.model.data;

import osmo.tester.model.VariableValue;

/**
 * This can be used to provide a {@literal @}Variable value that always is recorded before and after
 * a step (transition) as a variable value.
 * <br>
 * Example:
 * {@code private AtomicInteger hello = new AtomicInteger(0);}
 *
 * {@code @Variable private ToStringValue helloValue = new ToStringValue(hello);}
 * <br>
 *
 * <br>
 * This will result in one  variable being stored named "helloValue". At each point the string representation of "hello"
 * is modified, it will be stored as coverage value.
 *
 * @author Teemu Kanstren
 */
public class ToStringValue implements VariableValue {
  /** The object that should be observed. */
  private final Object target;

  /** @param target The object to be observed. */
  public ToStringValue(Object target) {
    this.target = target;
  }

  /**
   * Gives the value to be stored (observation target.toString()).
   *
   * @return String representation of observation target.
   */
  @Override
  public Object value() {
    return "" + target;
  }
}
