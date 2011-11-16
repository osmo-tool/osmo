package osmo.tester.parser;

/**
 * A model-object that represents test model elements.
 *
 * @author Teemu Kanstren
 */
public class ModelObject {
  /** Prefix of the model object, added to names of all parsed transitions, guards, etc. */
  private final String prefix;
  /** The model object, which implements a set of given transitions, guards, etc. to be invoked. */
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
