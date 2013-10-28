package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.BeforeSuite} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class BeforeSuiteParser implements AnnotationParser {
  private static Logger log = new Logger(BeforeSuiteParser.class);

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    BeforeSuite before = (BeforeSuite) parameters.getAnnotation();
    result.getFsm().addBeforeSuite(new InvocationTarget(parameters, BeforeSuite.class));
    Method method = parameters.getMethod();
    String errors = "";
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += "@" + BeforeSuite.class.getSimpleName() + " methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n";
    }
    return errors;
  }
}
