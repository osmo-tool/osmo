package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Method;

/**
 * Parses {@link AfterSuite} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class AfterSuiteParser implements AnnotationParser {
  private static Logger log = new Logger(AfterSuiteParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    AfterSuite after = (AfterSuite) parameters.getAnnotation();
    Method method = parameters.getMethod();
    parameters.getFsm().addAfterSuite(new InvocationTarget(parameters, AfterSuite.class));
    return "";
  }
}
