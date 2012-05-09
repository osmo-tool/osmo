package osmo.tester.model.dataflow;

/** @author Teemu Kanstren */
public interface OptionDeserializer<T> {
  public T deserialize(String value);
}
