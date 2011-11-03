package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.Pre;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Parses {@link osmo.tester.annotation.Pre} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class PreParser implements AnnotationParser {
  private static Logger log = new Logger(PreParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    Pre pre = (Pre) parameters.getAnnotation();

    Method method = parameters.getMethod();
    String errors = "";
    Class<?>[] parameterTypes = method.getParameterTypes();
    //return types are not checked because the make no difference for invocation
    if (parameterTypes.length > 1) {
      errors += "Pre-methods are allowed to have only one parameter of type Map<String, Object>: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n";
    }
    if (parameterTypes.length == 1) {
      Class<?> parameterType = parameterTypes[0];
      if (parameterType != Map.class) {
        errors += "Pre-methods are allowed to have only one parameter of type Map<String, Object>: \"" + method.getName() + "()\" has one of type " + parameterType + ".\n";
      }
    }

    InvocationTarget target = new InvocationTarget(parameters, Pre.class);
    FSM fsm = parameters.getFsm();
    String[] transitionNames = pre.value();
    String prefix = parameters.getPrefix();
    for (String name : transitionNames) {
      log.debug("Parsing pre-method '" + name + "'");
      //todo: add test for transition named "all" in a test model
      if (name.equals("all")) {
        fsm.addGenericPre(target);
        //generic pre-methods should not be have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        continue;
      }
      name = prefix+name;
      FSMTransition transition = fsm.createTransition(name, -1);
      transition.addPre(target);
    }
    return errors;
  }
}
