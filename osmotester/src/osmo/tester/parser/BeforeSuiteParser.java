package osmo.tester.parser;

import osmo.tester.annotation.BeforeSuite;
import osmo.tester.log.Logger;
import osmo.tester.model.InvocationTarget;

import java.lang.reflect.Method;

/**
 * Parses {@link BeforeSuite} annotations from the given model object.
 * 
 * @author Teemu Kanstren
 */
public class BeforeSuiteParser implements AnnotationParser {
  private static Logger log = new Logger(BeforeSuiteParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    BeforeSuite before = (BeforeSuite) parameters.getAnnotation();
    Method method = parameters.getMethod();
    parameters.getFsm().addBeforeSuite(new InvocationTarget(parameters, BeforeSuite.class));
    return "";
  }
}
