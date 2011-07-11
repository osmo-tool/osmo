package osmo.tester.parser;

import osmo.tester.annotation.EndCondition;
import osmo.tester.log.Logger;
import osmo.tester.model.InvocationTarget;

import java.lang.reflect.Method;

/**
 * Parses {@link EndCondition} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class EndConditionParser implements AnnotationParser {
  private static Logger log = new Logger(EndConditionParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    EndCondition ec = (EndCondition) parameters.getAnnotation();
    Method method = parameters.getMethod();
    Class<?> returnType = method.getReturnType();
    String errors = "";
    if (returnType != boolean.class && returnType != Boolean.class) {
      errors += "Invalid return type for @"+EndCondition.class.getSimpleName()+" (\""+method.getName()+"()\"):"+returnType+". Should be boolean.\n";
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += "@"+EndCondition.class.getSimpleName()+" methods are not allowed to have parameters: \""+method.getName()+"()\" has "+parameterTypes.length+" parameters.\n";
    }
    parameters.getFsm().addEndCondition(new InvocationTarget(parameters, EndCondition.class));
    return errors;
  }
}
