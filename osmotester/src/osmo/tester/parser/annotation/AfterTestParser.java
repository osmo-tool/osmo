package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.AfterTest;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.AfterTest} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class AfterTestParser implements AnnotationParser {
  private static Logger log = new Logger(AfterTestParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    AfterTest after = (AfterTest) parameters.getAnnotation();
    Method method = parameters.getMethod();
    parameters.getFsm().addAfter(new InvocationTarget(parameters, AfterTest.class));
    return "";
  }
}
