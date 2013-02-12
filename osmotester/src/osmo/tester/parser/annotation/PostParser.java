package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.Post;
import osmo.tester.model.FSM;
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
    if (parameterTypes.length == 1) {
      Class<?> parameterType = parameterTypes[0];
      if (parameterType != Map.class) {
        errors += "Post-methods are allowed to have only one parameter of type Map<String, Object>: \"" + method.getName() + "()\" has one of type " + parameterType + ".\n";
      }
    }

    InvocationTarget target = new InvocationTarget(parameters, Post.class);
    FSM fsm = result.getFsm();
    String[] transitionNames = oracle.value();
    String prefix = parameters.getPrefix();
    for (String name : transitionNames) {
      log.debug("Parsing post '" + name + "'");
      if (name.equals("all")) {
        fsm.addGenericPost(target);
        //generic post should not be have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        log.debug("added generic post:" + name);
        continue;
      }
      TransitionName tName = new TransitionName(prefix, name);
      log.debug("created specific post:" + name);
      FSMTransition transition = fsm.createTransition(tName, -1);
      transition.addPost(target);
    }
    return errors;
  }
}
