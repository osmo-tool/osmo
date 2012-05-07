package osmo.tester.parser.field;

import osmo.tester.model.dataflow.SearchableInput;
import osmo.tester.model.dataflow.SearchableInputField;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Field;

/**
 * Parses fields of the {@link SearchableInput} and stores them to the test model FSM.
 *
 * @author Teemu Kanstren
 */
public class SearchableInputParser implements AnnotationParser {
  @Override
  public String parse(ParserParameters parameters) {
    String errors = "";
    Field field = parameters.getField();
    field.setAccessible(true);
    Object model = parameters.getModel();
    parameters.getFsm().addSearchableInputField(new SearchableInputField(model, field));
    return errors;
  }
}
