package osmo.tester.parser.annotation;

import osmo.common.Logger;
import osmo.tester.annotation.BeforeStep;
import osmo.tester.annotation.Pre;
import osmo.tester.model.FSM;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.TransitionName;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.Pre} and {@link osmo.tester.annotation.BeforeStep} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class PreParser implements AnnotationParser {
  private static final Logger log = new Logger(PreParser.class);

  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    Object annotation = parameters.getAnnotation();
    Class type = null;
    String[] targetNames = null;
    if (annotation instanceof Pre) {
      Pre pre = (Pre) annotation;
      targetNames = pre.value();
      type = Pre.class;
    } else {
      BeforeStep bs = (BeforeStep) annotation;
      targetNames = bs.value();
      type = BeforeStep.class;
    }

    Method method = parameters.getMethod();
    Class<?>[] parameterTypes = method.getParameterTypes();
    String aName = "@" + type.getSimpleName();
    //return types are not checked because the make no difference for invocation
    if (parameterTypes.length > 0) {
      errors.append(aName).append(" methods are not allowed to have parameters: \"" + method.getName() + "()\" has " +
          parameterTypes.length + " parameters.\n");
    }

    InvocationTarget target = new InvocationTarget(parameters, type);
    FSM fsm = result.getFsm();
    String prefix = parameters.getPrefix();
    for (String targetName : targetNames) {
      log.d("Parsing " + aName + "-method '" + targetName + "'");
      targetName = GuardParser.checkMethodName(targetName, parameters, errors, aName);
      if (targetName.equals("all")) {
        fsm.addGenericPre(target);
        //generic pre-methods should not have their own transition or it will fail the FSM check since it is a guard without a transition
        continue;
      }
      TransitionName tName = new TransitionName(prefix, targetName);
      fsm.addSpecificPre(tName, target);
    }
  }
}
