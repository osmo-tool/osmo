package osmo.tester.parser.field;

import osmo.common.log.Logger;
import osmo.tester.model.data.SearchableInput;

import java.lang.reflect.Field;

/**
 * Represents a field for one of the OSMO dataflow modelling objects in the model.
 *
 * @author Teemu Kanstren
 */
public class SearchableInputField {
  private static Logger log = new Logger(SearchableInputField.class);
  /** The model object. */
  private final Object model;
  /** The field itself. */
  private final Field field;

  public SearchableInputField(Object model, Field field) {
    this.model = model;
    this.field = field;
  }

  /**
   * Get the field contents.
   *
   * @return The field contents.
   */
  public SearchableInput getInput() {
    SearchableInput input = null;
    try {
      input = (SearchableInput) field.get(model);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Failed to read " + SearchableInput.class.getSimpleName() + " field.", e);
    }
    if (input.getName() == null) {
      input.setName(field.getName());
    }
    return input;
  }

  public String getName() {
    return getInput().getName();
  }
}
