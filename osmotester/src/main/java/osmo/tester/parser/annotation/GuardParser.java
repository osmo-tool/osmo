package osmo.tester.parser.annotation;

import osmo.common.Logger;
import osmo.tester.annotation.Guard;
import osmo.tester.model.FSM;
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
  private static final Logger log = new Logger(GuardParser.class);

  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    Guard g = (Guard) parameters.getAnnotation();

    Method method = parameters.getMethod();
    String aName = "@"+Guard.class.getSimpleName();
    Class<?> returnType = method.getReturnType();
    if (returnType != boolean.class && returnType != Boolean.class) {
      errors.append("Invalid return type for guard (\"" + method.getName() + "()\"):" + returnType + ".\n");
    }
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors.append("Guard methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n");
    }

    String[] transitionNames = g.value();
    String prefix = parameters.getPrefix();
    for (String givenName : transitionNames) {
      FSM fsm = result.getFsm();
      InvocationTarget target = new InvocationTarget(parameters, Guard.class);
      givenName = checkMethodName(givenName, parameters, errors, aName);
      if (givenName.equals("all")) {
        //generic guards should not have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        fsm.addGenericGuard(target);
        //it should also have no other associations defined, as they should be already part of it all
        if (transitionNames.length > 1) {
          errors.append("A guard that is associated with 'all' transitions should not have any other associations defined. ");
          errors.append("One had " + Arrays.asList(transitionNames) + " as a list of associations.");
        }
        return;
      }
      if (givenName.startsWith("!")) {
        givenName = givenName.substring(1);
        if (givenName.length() == 0) {
          errors.append("Negation cannot exist without a name. You have a guard with only '!' as the name.");
          return;
        }
        TransitionName name = new TransitionName(prefix, givenName);
        fsm.addNegatedGuard(name, target);
        continue;
      }
      TransitionName name = new TransitionName(prefix, givenName);
      fsm.addSpecificGuard(name, target);
    }
  }

  public static String findNameFrom(String methodName) {
    char[] chars = methodName.toCharArray();
    int i = 0;
    for (char c : chars) {
      if (Character.isUpperCase(c)) break;
      i++;
    }
    return methodName.substring(i);
  }

  public static String checkMethodName(String targetName, ParserParameters parameters, StringBuilder errors, String aName) {
    if (targetName.equals(Guard.DEFAULT)) {
      if (parameters.isMethodBasedNaming()) {
        String methodName = parameters.getMethod().getName();
        targetName = GuardParser.findNameFrom(methodName);
        if (targetName.length() == 0) {
          String msg = aName + " method name must be of format xX when using method based naming: " + methodName;
          msg += ". Or if using generic association, name \"all\" must be used.\n";
          errors.append(msg);
        }
      } else {
        targetName = "all";
      }
    }
    return targetName;
  }
}
