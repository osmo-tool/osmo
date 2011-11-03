package osmo.tester.parser;

/** @author Teemu Kanstren */
public class ModelObject {
  private final String prefix;
  private final Object object;

  public ModelObject(String prefix, Object object) {
    this.prefix = prefix;
    this.object = object;
  }

  public ModelObject(Object object) {
    this.prefix = "";
    this.object = object;
  }

  public String getPrefix() {
    return prefix;
  }

  public Object getObject() {
    return object;
  }
}
