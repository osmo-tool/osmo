package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
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
  public String parse(ParserResult result, ParserParameters parameters) {
    OnError ec = (OnError) parameters.getAnnotation();
    Method method = parameters.getMethod();
    Class<?> returnType = method.getReturnType();
    String errors = "";
    String name = OnError.class.getSimpleName();
    if (returnType != void.class && returnType != Void.class) {
      errors += "Invalid return type for @" + name + " (\"" + method.getName() + "()\"):" + returnType + ". Should be void.\n";
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += "@" + name + " methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n";
    }
    result.getFsm().addOnError(new InvocationTarget(parameters, OnError.class));
    return errors;
  }
}
