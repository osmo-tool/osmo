package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
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
  private static Logger log = new Logger(GroupParser.class);
  
  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    Group cg = (Group) parameters.getAnnotation();
    String value = cg.value();
    String annotationName = Group.class.getSimpleName();
    if (value == null) {
      return "@"+annotationName + " value cannot be null.\n";
    }
    if (value.length() == 0) {
      return "@"+annotationName + " must have name.\n";
    }
    parameters.addClassAnnotation(annotationName, value); 
    return "";
  }
}
