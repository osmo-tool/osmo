package osmo.tester.model.dataflow;

/** @author Teemu Kanstren */
public class StringDeserializer implements OptionDeserializer<String> {
  @Override
  public String deserialize(String value) {
    return value;
  }
}
