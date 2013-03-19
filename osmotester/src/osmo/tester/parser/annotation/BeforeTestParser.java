package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

/**
 * Parses {@link osmo.tester.annotation.BeforeTest} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class BeforeTestParser implements AnnotationParser {
  private static Logger log = new Logger(BeforeTestParser.class);

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    BeforeTest before = (BeforeTest) parameters.getAnnotation();
    result.getFsm().addBefore(new InvocationTarget(parameters, BeforeTest.class));
    return "";
  }
}
