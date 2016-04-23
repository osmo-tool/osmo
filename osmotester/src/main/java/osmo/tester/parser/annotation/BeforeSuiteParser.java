package osmo.tester.parser.annotation;

import osmo.common.Logger;
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
  private static final Logger log = new Logger(BeforeSuiteParser.class);

  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    BeforeSuite before = (BeforeSuite) parameters.getAnnotation();
    result.getFsm().addBeforeSuite(new InvocationTarget(parameters, BeforeSuite.class));
    Method method = parameters.getMethod();
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors.append("@" + BeforeSuite.class.getSimpleName() + " methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n");
    }
  }
}
