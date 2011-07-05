package osmo.tester.parser;

import osmo.tester.annotation.After;
import osmo.tester.log.Logger;
import osmo.tester.model.InvocationTarget;

import java.lang.reflect.Method;

/**
 * Parses {@link After} annotations from the given model object.
 * 
 * @author Teemu Kanstren
 */
public class AfterParser implements AnnotationParser {
  private static Logger log = new Logger(AfterParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    After after = (After) parameters.getAnnotation();
    Method method = parameters.getMethod();
    parameters.getFsm().addAfter(new InvocationTarget(parameters, After.class));
    return "";
  }
}
