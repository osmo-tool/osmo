package osmo.tester.parser.annotation;

import osmo.common.Logger;
import osmo.tester.annotation.AfterStep;
import osmo.tester.annotation.Post;
import osmo.tester.model.FSM;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.TransitionName;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;
import osmo.tester.parser.ParserResult;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.Post} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class PostParser implements AnnotationParser {
  private static final Logger log = new Logger(PostParser.class);

  @Override
  public void parse(ParserResult result, ParserParameters parameters, StringBuilder errors) {
    Object annotation = parameters.getAnnotation();
    Class type = null;
    String[] targetNames = null;
    if (annotation instanceof Post) {
      Post post = (Post) annotation;
      targetNames = post.value();
      type = Post.class;
    } else {
      AfterStep as = (AfterStep) annotation;
      targetNames = as.value();
      type = AfterStep.class;
    }

    Method method = parameters.getMethod();
    String aName = "@"+type.getSimpleName();
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors.append(aName+" methods are not allowed to have any parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + ".\n");
    }

    InvocationTarget target = new InvocationTarget(parameters, Post.class);
    FSM fsm = result.getFsm();
    String prefix = parameters.getPrefix();
    for (String targetName : targetNames) {
      log.d("Parsing post '" + targetName + "'");
      targetName = GuardParser.checkMethodName(targetName, parameters, errors, aName);
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
  }
}
