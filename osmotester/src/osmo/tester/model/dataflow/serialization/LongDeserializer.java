package osmo.tester.model.dataflow.serialization;

/** @author Teemu Kanstren */
public class LongDeserializer implements Deserializer<Long> {
  @Override
  public Long deserialize(String serialized) {
    return Long.parseLong(serialized);
  }
}
