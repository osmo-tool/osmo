package osmo.tester.model.dataflow.serialization;

/** @author Teemu Kanstren */
public class FloatDeserializer implements Deserializer<Float> {
  @Override
  public Float deserialize(String serialized) {
    return Float.parseFloat(serialized);
  }
}
