package osmo.miner.model;

/**
 * @author Teemu Kanstren
 */
public class ValuePair {
  private final String name;
  private final Object value;

  public ValuePair(String name, Object value) {
    super();
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    return value;
  }}
