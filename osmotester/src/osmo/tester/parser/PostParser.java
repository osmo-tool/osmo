package osmo.tester.parser;

import osmo.tester.annotation.Post;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;

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
  public String parse(ParserParameters parameters) {
    Post oracle = (Post) parameters.getAnnotation();

    Method method = parameters.getMethod();
    String errors = "";
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length == 1) {
      Class<?> parameterType = parameterTypes[0];
      if (parameterType != Map.class) {
        errors += "Post-methods are allowed to have only one parameter of type Map<String, Object>: \""+method.getName()+"()\" has one of type "+parameterType+".\n";
      }
    }

    InvocationTarget target = new InvocationTarget(parameters, Post.class);
    FSM fsm = parameters.getFsm();
    String[] transitionNames = oracle.value();
    for (String name : transitionNames) {
      log.debug("Parsing post '"+name+"'");
      //todo: add test for transition named "all" in a test model
      if (name.equals("all")) {
        fsm.addGenericPost(target);
        //generic post should not be have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        continue;
      }
      FSMTransition transition = fsm.createTransition(name, -1);
      transition.addPost(target);
    }
    return errors;
  }
}
