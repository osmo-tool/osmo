package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.AfterSuite;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.AfterSuite} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class AfterSuiteParser implements AnnotationParser {
  private static final Logger log = new Logger(AfterSuiteParser.class);

  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    AfterSuite after = (AfterSuite) parameters.getAnnotation();
    Method method = parameters.getMethod();
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors.append("@" + AfterSuite.class.getSimpleName() + " methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n");
    }
    result.getFsm().addAfterSuite(new InvocationTarget(parameters, AfterSuite.class));
  }
}
