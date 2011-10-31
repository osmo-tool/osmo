package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.EndState;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Method;

/**
 * Parses {@link EndState} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class EndStateParser implements AnnotationParser {
  private static Logger log = new Logger(EndStateParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    EndState ec = (EndState) parameters.getAnnotation();
    Method method = parameters.getMethod();
    Class<?> returnType = method.getReturnType();
    String errors = "";
    if (returnType != boolean.class && returnType != Boolean.class) {
      errors += "Invalid return type for @" + EndState.class.getSimpleName() + " (\"" + method.getName() + "()\"):" + returnType + ". Should be boolean.\n";
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += "@" + EndState.class.getSimpleName() + " methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n";
    }
    parameters.getFsm().addEndState(new InvocationTarget(parameters, EndState.class));
    return errors;
  }
}
