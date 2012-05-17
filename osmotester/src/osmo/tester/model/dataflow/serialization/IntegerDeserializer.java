package osmo.tester.model.dataflow.serialization;

/** @author Teemu Kanstren */
public class IntegerDeserializer implements Deserializer<Integer> {
  @Override
  public Integer deserialize(String serialized) {
    return Integer.parseInt(serialized);
  }
}
