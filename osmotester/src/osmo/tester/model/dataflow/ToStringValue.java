package osmo.tester.model.dataflow;

import osmo.tester.model.VariableValue;

/**
 * This can be used to provide a &#064;Variable value that always is recorded before and after
 * a step (transition) as a variable value.
 * <p/>
 * Example:
 * private AtomicInteger hello = new AtomicInteger(0);
 * <p/>
 * &#064;Variable private ToStringValue helloValue = new ToStringValue(hello);
 * <p/>
 * <p/>
 * This will result in one  variable being stored named "helloValue". At each point the string representation of "hello"
 * is modified, it will be stored as value between steps (transitions).
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
