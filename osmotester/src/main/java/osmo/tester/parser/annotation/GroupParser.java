package osmo.tester.parser.annotation;

import osmo.common.Logger;
import osmo.tester.annotation.Group;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

/**
 * Parses {@link osmo.tester.annotation.Group} annotations from the given model object.
 *
 * @author Teemu Kanstren 
 */
public class GroupParser implements AnnotationParser {
  private static final Logger log = new Logger(GroupParser.class);
  
  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    Group cg = (Group) parameters.getAnnotation();
    String value = cg.value();
    //this name is also used to store/access the annotation by class type elsewhere so we cannot add @ to it
    String name = Group.class.getSimpleName();
    if (value == null) {
      errors.append("@"+name + " value cannot be null.\n");
      return;
    }
    if (value.length() == 0) {
      errors.append("@"+name + " must have name.\n");
      return;
    }
    parameters.addClassAnnotation(name, value); 
  }
}
