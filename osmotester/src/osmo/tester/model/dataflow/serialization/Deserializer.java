package osmo.tester.model.dataflow.serialization;

/** @author Teemu Kanstren */
public interface Deserializer<T> {
  public T deserialize(String serialized);
}
