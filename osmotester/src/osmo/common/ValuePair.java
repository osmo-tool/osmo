package osmo.common;

/**
 * @author Teemu Kanstren
 */
public class ValuePair <T> {
  private final T value1;
  private final T value2;

  public ValuePair(T value1, T value2) {
    super();
    this.value1 = value1;
    this.value2 = value2;
  }

  public T getValue1() {
    return value1;
  }

  public T getValue2() {
    return value2;
  }
}
