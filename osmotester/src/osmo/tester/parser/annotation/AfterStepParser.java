package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.AfterStep;
import osmo.tester.annotation.Group;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.model.FSM;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.TransitionName;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link Post} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class AfterStepParser implements AnnotationParser {
  private static final Logger log = new Logger(AfterStepParser.class);

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    AfterStep post = (AfterStep) parameters.getAnnotation();

    Method method = parameters.getMethod();
    String errors = "";
    String aName = "@"+AfterStep.class.getSimpleName();
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += aName+" methods are not allowed to have any parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + ".\n";
    }

    InvocationTarget target = new InvocationTarget(parameters, AfterStep.class);
    FSM fsm = result.getFsm();
    String[] targetNames = post.value();
    String prefix = parameters.getPrefix();
    String group = parameters.getClassAnnotation(Group.class);
    for (String targetName : targetNames) {
      log.d("Parsing post '" + targetName + "'");
      if (targetName.equals(Guard.DEFAULT)) {
        //If no name is given to pre/post but a group is defined for their class, we use that as our target
        if (group.length() > 0) {
          targetName = group;
        } else {
          String methodName = parameters.getMethod().getName();
          targetName = GuardParser.findNameFrom(methodName);
          if (targetName.length() == 0) {
            String msg = aName + " method name must be of format xX when using method based naming: " + methodName;
            msg += ". Or if using generic association, name \"all\" must be used.\n";
            errors += msg;
          }
        }
      }
      if (targetName.equals("all")) {
        fsm.addGenericPost(target);
        //generic post should not be have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        log.d("added generic post:" + targetName);
        continue;
      }
      TransitionName tName = new TransitionName(prefix, targetName);
      log.d("created specific post:" + targetName);
      fsm.addSpecificPost(tName, target);
    }
    return errors;
  }
}
