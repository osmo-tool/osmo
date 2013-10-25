package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.Group;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMGuard;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.TransitionName;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Parses {@link osmo.tester.annotation.Post} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class PostParser implements AnnotationParser {
  private static Logger log = new Logger(PostParser.class);

  @Override
  public String parse(ParserResult result, ParserParameters parameters) {
    Post oracle = (Post) parameters.getAnnotation();

    Method method = parameters.getMethod();
    String errors = "";
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += "Post-methods are not allowed to have any parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + ".\n";
    }

    InvocationTarget target = new InvocationTarget(parameters, Post.class);
    FSM fsm = result.getFsm();
    String[] transitionNames = oracle.value();
    String prefix = parameters.getPrefix();
    String group = parameters.getClassAnnotation(Group.class);
    for (String name : transitionNames) {
      log.debug("Parsing post '" + name + "'");
      if (name.equals(Guard.DEFAULT)) {
        //If no name is given to pre/post but a group is defined for their class, we use that as our target
        if (group.length() > 0) {
          name = group;
        } else {
          String methodName = parameters.getMethod().getName();
          name = GuardParser.findNameFrom(methodName);
          if (name.length() == 0)
            errors += "Post method name must be of format xX when using method based naming: " + methodName;
        }
      }
      if (name.equals("all")) {
        fsm.addGenericPost(target);
        //generic post should not be have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        log.debug("added generic post:" + name);
        continue;
      }
      TransitionName tName = new TransitionName(prefix, name);
      log.debug("created specific post:" + name);
      fsm.addSpecificPost(tName, target);
    }
    return errors;
  }
}
