package osmo.tester.parser;

import osmo.tester.annotation.BeforeTest;
import osmo.tester.log.Logger;
import osmo.tester.model.InvocationTarget;

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
