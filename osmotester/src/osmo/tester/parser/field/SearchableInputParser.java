package osmo.tester.parser.field;

import osmo.tester.annotation.Variable;
import osmo.tester.model.data.SearchableInput;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Parses fields of the {@link osmo.tester.model.data.SearchableInput} type and stores them to the test model FSM.
 *
 * @author Teemu Kanstren
 */
public class SearchableInputParser implements AnnotationParser {
  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    Field field = parameters.getField();
    field.setAccessible(true);
    Object model = parameters.getModel();
    String name = SearchableInput.class.getSimpleName();
    SearchableInput input = null;
    try {
      input = (SearchableInput) field.get(model);
      if (input == null) {
        errors.append(name+" must be initialized when defined:" + field.getName() + ".\n");
        return;
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to parse "+name+" object " + field.getName(), e);
    }
    input.setSuite(parameters.getSuite());
    input.setSeed(parameters.getSeed());
    if (input.getName() == null) {
      input.setName(field.getName());
    }

    Collection<Object> annotations = parameters.getFieldAnnotations();
    for (Object annotation : annotations) {
      if (annotation instanceof Variable) {
        input.setStored(true);
        Variable var = (Variable) annotation;
        if (var.value().length() > 0) {
          input.setName(var.value());
        }
      }
    }
  }
}
