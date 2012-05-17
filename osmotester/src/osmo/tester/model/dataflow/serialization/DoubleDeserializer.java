package osmo.tester.model.dataflow.serialization;

/** @author Teemu Kanstren */
public class DoubleDeserializer implements Deserializer<Double> {
  @Override
  public Double deserialize(String serialized) {
    return Double.parseDouble(serialized);
  }
}
