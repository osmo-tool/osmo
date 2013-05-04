package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.RequirementsField;
import osmo.tester.model.Requirements;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Field;

/**
 * Parses {@link osmo.tester.annotation.RequirementsField} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class RequirementsFieldParser implements AnnotationParser {
  private static Logger log = new Logger(RequirementsFieldParser.class);
  private Requirements req = null;

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    String errors = "";
    String name = "@" + RequirementsField.class.getSimpleName();
    RequirementsField annotation = (RequirementsField) parameters.getAnnotation();
    try {
      Field field = parameters.getField();
      Class<?> type = field.getType();
      if (type != Requirements.class) {
        errors += name + " class must be of type " + Requirements.class.getName() + ". Was " + type.getName() + ".\n";
        return errors;
      }
      //to enable setting private fields
      field.setAccessible(true);
      Object model = parameters.getModel();
      Requirements requirements = (Requirements) field.get(model);
      if (requirements == null) {
        errors += name + " value was null, which is not allowed.\n";
        return errors;
      }
      if (this.req != null && this.req != requirements) {
        errors += "Only one " + name + " instance allowed in the model.\n";
      }
      result.setRequirements(requirements);
      this.req = requirements;
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to parse/set " + name, e);
    }
    return errors;
  }
}
