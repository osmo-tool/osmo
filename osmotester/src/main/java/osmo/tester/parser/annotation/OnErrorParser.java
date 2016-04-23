package osmo.tester.parser.annotation;

import osmo.common.Logger;
import osmo.tester.annotation.EndCondition;
import osmo.tester.annotation.OnError;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link EndCondition} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class OnErrorParser implements AnnotationParser {
  private static final Logger log = new Logger(OnErrorParser.class);

  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    OnError ec = (OnError) parameters.getAnnotation();
    Method method = parameters.getMethod();
    Class<?> returnType = method.getReturnType();
    String name = OnError.class.getSimpleName();
    if (returnType != void.class && returnType != Void.class) {
      errors.append("Invalid return type for @" + name + " (\"" + method.getName() + "()\"):" + returnType + ". Should be void.\n");
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors.append("@" + name + " methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n");
    }
    result.getFsm().addOnError(new InvocationTarget(parameters, OnError.class));
  }
}
