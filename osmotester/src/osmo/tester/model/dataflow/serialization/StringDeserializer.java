package osmo.tester.model.dataflow.serialization;

import osmo.tester.model.dataflow.serialization.Deserializer;

/** @author Teemu Kanstren */
public class StringDeserializer implements Deserializer<String> {
  @Override
  public String deserialize(String serialized) {
    return serialized;
  }
}
