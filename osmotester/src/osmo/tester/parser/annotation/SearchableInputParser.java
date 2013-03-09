package osmo.tester.parser.annotation;

import osmo.tester.annotation.Variable;
import osmo.tester.model.data.SearchableInput;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;
import osmo.tester.parser.field.SearchableInputField;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Parses fields of the {@link osmo.tester.model.data.SearchableInput} and stores them to the test model FSM.
 *
 * @author Teemu Kanstren
 */
public class SearchableInputParser implements AnnotationParser {
  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    String errors = "";
    Field field = parameters.getField();
    field.setAccessible(true);
    Object model = parameters.getModel();
    SearchableInput input = null;
    try {
      input = (SearchableInput) field.get(model);
      if (input == null) {
        return "SearchableInput must be initialized when defined:" + field.getName() + ".\n";
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to parse SearchableInput object " + field.getName(), e);
    }
    SearchableInputField inputField = new SearchableInputField(model, field);
    inputField.getInput().setSuite(parameters.getSuite());
    Collection<Object> annotations = parameters.getFieldAnnotations();
    for (Object annotation : annotations) {
      if (annotation instanceof Variable) {
        Variable var = (Variable) annotation;
        if (var.value().length() > 0) {
          input.setName(var.value());
        }
      }
    }
    result.getFsm().addSearchableInputField(inputField);
    return errors;
  }
}
