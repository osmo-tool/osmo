package osmo.tester.parser;

import osmo.tester.annotation.RequirementsField;
import osmo.tester.annotation.Variable;
import osmo.tester.log.Logger;
import osmo.tester.model.Requirements;
import osmo.tester.model.VariableField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Parses {@link osmo.tester.annotation.Variable} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class VariableParser implements AnnotationParser {
  private static Logger log = new Logger(VariableParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    String errors = "";
    String name = "@"+Variable.class.getSimpleName();
//    Variable annotation = (Variable) parameters.getAnnotation();
    Field field = parameters.getField();
    //we bypass the private etc. modifiers to access it
    field.setAccessible(true);
    Object model = parameters.getModel();
    VariableField var = new VariableField(model, field);
    parameters.getFsm().addVariable(var);
    log.debug("Parsed variable:"+name);
    return errors;
  }
}
