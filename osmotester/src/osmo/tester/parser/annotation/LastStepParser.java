package osmo.tester.parser.annotation;

import osmo.common.Logger;
import osmo.tester.annotation.LastStep;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.LastStep} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class LastStepParser implements AnnotationParser {
  private static final Logger log = new Logger(LastStepParser.class);

  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    LastStep after = (LastStep) parameters.getAnnotation();
    InvocationTarget target = new InvocationTarget(parameters, LastStep.class);
    result.getFsm().addLastStep(target);
    Method method = parameters.getMethod();
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors.append("@" + LastStep.class.getSimpleName() + " methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n");
    }
  }
}
