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
  /** If the variable object implements the VariableValue interface, we store it here for faster access. */
  private VariableValue variable = null;

  public VariableField(Object modelObject, Field field) {
    this.modelObject = modelObject;
    this.field = field;
    name = field.getName();
    ///check only once here to avoid overhead of repeating it on every access
    checkIfVariableValue();
  }

  private void checkIfVariableValue() {
    Class<?>[] interfaces = field.getType().getInterfaces();
    for (Class<?> i : interfaces) {
      //we assume same classloader and use ==, most naughty
      if (i == VariableValue.class) {
        try {
          variable = (VariableValue) field.get(modelObject);
        } catch (IllegalAccessException e) {
          throw new RuntimeException("Failed to read state variable value for field:"+field, e);
        }
        break;
      }
    }
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    if (variable != null) {
      return variable.value();
    }
    try {
      return field.get(modelObject);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Failed to read state variable value for field:"+field, e);
    }
  }
}
