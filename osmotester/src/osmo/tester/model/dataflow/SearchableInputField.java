package osmo.tester.model.dataflow;

import osmo.common.log.Logger;

import java.lang.reflect.Field;

/** @author Teemu Kanstren */
public class SearchableInputField {
  private static Logger log = new Logger(SearchableInputField.class);
  private final Object model;
  private final Field field;

  public SearchableInputField(Object model, Field field) {
    this.model = model;
    this.field = field;
  }

  public SearchableInput getInput() {
    SearchableInput input = null;
    try {
      input = (SearchableInput) field.get(model);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Failed to read " + SearchableInput.class.getSimpleName() + " field.", e);
    }
    String name = field.getName();
    if (input == null) {
      log.warn(name + " value was null. SearchableInput must be initialized before parsing.\n");
    }
    if (input.getName() == null) {
      input.setName(name);
    }
    return input;
  }

  public String getName() {
    return field.getName();
  }
}
