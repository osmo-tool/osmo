package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.Guard;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.TransitionName;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Parses {@link osmo.tester.annotation.Guard} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class GuardParser implements AnnotationParser {
  private static Logger log = new Logger(GuardParser.class);

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
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
    String prefix = parameters.getPrefix();
    for (String givenName : transitionNames) {
      FSM fsm = result.getFsm();
      InvocationTarget target = new InvocationTarget(parameters, Guard.class);
      if (givenName.equals("all")) {
        //generic guards should not have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        fsm.addGenericGuard(target);
        //it should also have no other associations defined, as they should be already part of it all
        if (transitionNames.length > 1) {
          errors += "A guard that is associated with 'all' transitions should not have any other associations defined. ";
          errors += "One had " + Arrays.asList(transitionNames) + " as a list of associations.";
        }
        return errors;
      }
      if (givenName.startsWith("!")) {
        givenName = givenName.substring(1);
        if (givenName.length() == 0) {
          errors += "Negation cannot exist without a name. You have a guard with only '!' as the name.";
          return errors;
        }
        TransitionName name = new TransitionName(prefix, givenName);
        fsm.addNegatedGuard(name, target);
        continue;
      }
      TransitionName name = new TransitionName(prefix, givenName);
      FSMTransition transition = fsm.createTransition(name, -1);
      transition.addGuard(target);
    }
    return errors;
  }
}
