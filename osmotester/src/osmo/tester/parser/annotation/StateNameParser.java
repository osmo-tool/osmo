package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.StateName;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.StateName} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class StateNameParser implements AnnotationParser {
  private static Logger log = new Logger(StateNameParser.class);

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    StateName state = (StateName) parameters.getAnnotation();

    Method method = parameters.getMethod();
    String errors = "";
    Class<?> returnType = method.getReturnType();
    String name = StateName.class.getSimpleName();
    if (returnType != String.class) {
      errors += "Invalid return type for @"+name+" in (\"" + method.getName() + "()\"):" + returnType + ". Should be String.\n";
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += name +" methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n";
    }
    
    if (result.getFsm().getStateDescription() != null) {
      errors += name+" is only expected to appear once.\n";
    }

    result.getFsm().setStateDescription(new InvocationTarget(parameters, StateName.class));
    return errors;
  }
}
