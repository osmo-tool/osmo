package osmo.tester.parser.field;

import osmo.common.log.Logger;
import osmo.tester.model.Requirements;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Field;

/**
 * Parses {@link osmo.tester.model.Requirements} objects from the given model object.
 *
 * @author Teemu Kanstren
 */
public class RequirementsParser implements AnnotationParser {
  private static final Logger log = new Logger(RequirementsParser.class);
  /** We store any found requirements object here so we can fail if two different instances are found. */
  private Requirements req = null;

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    String errors = "";
    Field field = parameters.getField();
    //to enable access to private fields
    field.setAccessible(true);
    Object model = parameters.getModel();
    String name = Requirements.class.getSimpleName();
    try {
      Requirements req = (Requirements) field.get(model);
      if (req == null) {
        errors += name+" object was null, which is not allowed.\n";
        return errors;
      }
      if (this.req != null && this.req != req) {
        errors += "Only one "+name+" object instance allowed in the model.\n";
      }
      result.setRequirements(req);
      this.req = req;
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to parse/set "+name+" object:"+field.getName(), e);
    }
    return errors;
  }
}
