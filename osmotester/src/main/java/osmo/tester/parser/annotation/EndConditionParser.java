package osmo.tester.parser.annotation;

import osmo.common.Logger;
import osmo.tester.annotation.EndCondition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.EndCondition} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class EndConditionParser implements AnnotationParser {
  private static final Logger log = new Logger(EndConditionParser.class);

  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    EndCondition ec = (EndCondition) parameters.getAnnotation();
    Method method = parameters.getMethod();
    Class<?> returnType = method.getReturnType();
    String name = EndCondition.class.getSimpleName();
    if (returnType != boolean.class && returnType != Boolean.class) {
      errors.append("Invalid return type for @" + name + " (\"" + method.getName() + "()\"):" + returnType + ". Should be boolean.\n");
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors.append("@" + name + " methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n");
    }
    result.getFsm().addEndCondition(new InvocationTarget(parameters, EndCondition.class));
  }
}
