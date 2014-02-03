package osmo.tester.parser.annotation;

import osmo.tester.annotation.Description;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

/**
 * @author Teemu Kanstren
 */
public class DescriptionParser implements AnnotationParser {
  
  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    Description annotation = (Description) parameters.getAnnotation();
    String text = annotation.value();
    result.addDescription(new InvocationTarget(parameters, Description.class), text);
    return "";
  }
}
