package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.Group;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Pre;
import osmo.tester.model.FSM;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.TransitionName;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.Pre} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class PreParser implements AnnotationParser {
  private static Logger log = new Logger(PreParser.class);

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    Pre pre = (Pre) parameters.getAnnotation();

    Method method = parameters.getMethod();
    String errors = "";
    Class<?>[] parameterTypes = method.getParameterTypes();
    //return types are not checked because the make no difference for invocation
    if (parameterTypes.length > 0) {
      errors += "Pre-methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n";
    }

    InvocationTarget target = new InvocationTarget(parameters, Pre.class);
    FSM fsm = result.getFsm();
    String[] transitionNames = pre.value();
    String prefix = parameters.getPrefix();
    String group = parameters.getClassAnnotation(Group.class);
    for (String name : transitionNames) {
      log.debug("Parsing pre-method '" + name + "'");
      if (name.equals(Guard.DEFAULT)) {
        //If no name is given to pre/post but a group is defined for their class, we use that as our target
        if (group.length() > 0) {
          name = group;
        } else {
          String methodName = parameters.getMethod().getName();
          name = GuardParser.findNameFrom(methodName);
          if (name.length() == 0) errors += "Pre method name must be of format xX when using method based naming: "+methodName;
        }
      }
      if (name.equals("all")) {
        fsm.addGenericPre(target);
        //generic pre-methods should not be have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        continue;
      }
      TransitionName tName = new TransitionName(prefix, name);
      fsm.addSpecificPre(tName, target);
    }
    return errors;
  }
}
