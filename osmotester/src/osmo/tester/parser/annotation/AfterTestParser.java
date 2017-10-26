package osmo.tester.parser.annotation;

import osmo.common.Logger;
import osmo.tester.annotation.AfterTest;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.AfterTest} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class AfterTestParser implements AnnotationParser {
  private static final Logger log = new Logger(AfterTestParser.class);

  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    AfterTest after = (AfterTest) parameters.getAnnotation();
    result.getFsm().addAfter(new InvocationTarget(parameters, AfterTest.class));
    Method method = parameters.getMethod();
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors.append("@").append(AfterTest.class.getSimpleName()).append(" methods are not allowed to have parameters:" +
              " \"").append(method.getName()).append("()\" has ").append(parameterTypes.length).append(" parameters.\n");
    }
  }
}