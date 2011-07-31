package osmo.tester.model;

import osmo.tester.log.Logger;

import java.lang.reflect.Field;

/**
 * Represents a field for a state variable in the model.
 *
 * @author Teemu Kanstren
 */
public class VariableField {
  private static Logger log = new Logger(VariableField.class);
  /** The model object itself, hosting the actual variable object. */
  private final Object modelObject;
  /** The field for the variable. */
  private final Field field;
  /** Field name, for faster access. */
  private final String name;

  public VariableField(Object modelObject, Field field) {
    this.modelObject = modelObject;
    this.field = field;
    name = field.getName();
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    try {
      return field.get(modelObject);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Failed to read state variable value for field:"+field, e);
    }
  }
}
