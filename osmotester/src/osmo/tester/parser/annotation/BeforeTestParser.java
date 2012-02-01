package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.BeforeTest} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class BeforeTestParser implements AnnotationParser {
  private static Logger log = new Logger(BeforeTestParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    BeforeTest before = (BeforeTest) parameters.getAnnotation();
    Method method = parameters.getMethod();
    parameters.getFsm().addBefore(new InvocationTarget(parameters, BeforeTest.class));
    return "";
  }
}
