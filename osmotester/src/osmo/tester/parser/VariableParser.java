package osmo.tester.parser;

import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.Variable;
import osmo.tester.model.Requirements;
import osmo.tester.model.VariableField;

import java.lang.reflect.Field;

/**
 * Parses {@link osmo.tester.annotation.Variable} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class VariableParser implements AnnotationParser {
  @Override
  public String parse(ParserParameters parameters) {
    String errors = "";
    String name = "@"+Variable.class.getSimpleName();
//    Variable annotation = (Variable) parameters.getAnnotation();
    Field field = parameters.getField();
    Class<?> type = field.getType();
    if (type != Variable.class) {
      errors += name+" class must be of type "+Variable.class.getName()+". Was "+type.getName()+".\n";
      return errors;
    }
    Object model = parameters.getModel();
    VariableField var = new VariableField(model, field);
    parameters.getFsm().addVariable(var);
    return errors;
  }
}
