package osmo.tester.parser.annotation;

import osmo.tester.annotation.Description;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

/**
 * Parses a {@link osmo.tester.annotation.Description} annotation from test model.
 * 
 * @author Teemu Kanstren
 */
public class DescriptionParser implements AnnotationParser {
  
  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    Description annotation = (Description) parameters.getAnnotation();
    String text = annotation.value();
    result.addDescription(new InvocationTarget(parameters, Description.class), text);
  }
}
