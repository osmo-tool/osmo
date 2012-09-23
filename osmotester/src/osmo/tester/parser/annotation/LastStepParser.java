package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.EndState;
import osmo.tester.annotation.LastStep;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Method;

/**
 * Parses {@link LastStep} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class LastStepParser implements AnnotationParser {
  private static Logger log = new Logger(LastStepParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    LastStep ls = (LastStep) parameters.getAnnotation();
    Method method = parameters.getMethod();
    String errors = "";
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += "@" + LastStep.class.getSimpleName() + " methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n";
    }
    parameters.getFsm().addLastStep(new InvocationTarget(parameters, LastStep.class));
    return errors;
  }
}
