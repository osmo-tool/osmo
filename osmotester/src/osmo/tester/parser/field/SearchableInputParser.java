package osmo.tester.parser.field;

import osmo.tester.model.dataflow.SearchableInput;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Field;

/** @author Teemu Kanstren */
public class SearchableInputParser implements AnnotationParser {
  @Override
  public String parse(ParserParameters parameters) {
    String errors = "";
    Field field = parameters.getField();
    field.setAccessible(true);
    Object model = parameters.getModel();
    SearchableInput input = null;
    try {
      input = (SearchableInput) field.get(model);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Failed to read " + SearchableInput.class.getSimpleName() + " field.", e);
    }
    if (input == null) {
      errors += field.getName() + " value was null, which is not allowed.\n";
      return errors;
    }
    if (input.getName() == null) {
      input.setName(field.getName());
    }
    parameters.getFsm().addSearchableInput(input);
    return errors;
  }
}
