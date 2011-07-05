package osmo.tester.parser;

import osmo.tester.annotation.Before;
import osmo.tester.log.Logger;
import osmo.tester.model.InvocationTarget;

import java.lang.reflect.Method;

/**
 * Parses {@link Before} annotations from the given model object.
 * 
 * @author Teemu Kanstren
 */
public class BeforeParser implements AnnotationParser {
  private static Logger log = new Logger(BeforeParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    Before before = (Before) parameters.getAnnotation();
    Method method = parameters.getMethod();
    parameters.getFsm().addBefore(new InvocationTarget(parameters, Before.class));
    return "";
  }
}
