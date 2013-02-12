package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.GenerationEnabler;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.GenerationEnabler} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class GenerationEnablerParser implements AnnotationParser {
  private static Logger log = new Logger(GenerationEnablerParser.class);

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    GenerationEnabler enabler = (GenerationEnabler) parameters.getAnnotation();
    Method method = parameters.getMethod();
    String errors = "";
    Class<?> returnType = method.getReturnType();
    String name = "@" + GenerationEnabler.class.getSimpleName();
    if (returnType != void.class && returnType != Void.class) {
      errors += "Invalid return type for " + name + " (\"" + method.getName() + "()\"):" + returnType + ".\n";
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += name + " methods are not allowed to have parameters: \"" + method.getName() + "()\" has " +
              parameterTypes.length + " parameters.\n";
    }

    result.getFsm().addGenerationEnabler(new InvocationTarget(parameters, GenerationEnabler.class));
    return errors;
  }
}
