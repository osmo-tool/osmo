package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.Guard;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Method;

/**
 * Parses {@link Guard} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class GuardParser implements AnnotationParser {
  private static Logger log = new Logger(GuardParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    Guard g = (Guard) parameters.getAnnotation();

    Method method = parameters.getMethod();
    String errors = "";
    Class<?> returnType = method.getReturnType();
    if (returnType != boolean.class && returnType != Boolean.class) {
      errors += "Invalid return type for guard (\"" + method.getName() + "()\"):" + returnType + ".\n";
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += "Guard methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n";
    }

    String[] transitionNames = g.value();
    for (String name : transitionNames) {
      FSM fsm = parameters.getFsm();
      InvocationTarget target = new InvocationTarget(parameters, Guard.class);
      if (name.equals("all")) {
        fsm.addGenericGuard(target);
        //generic guards should not have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        return errors;
      }
      FSMTransition transition = fsm.createTransition(name, -1);
      transition.addGuard(target);
    }
    return errors;
  }
}
