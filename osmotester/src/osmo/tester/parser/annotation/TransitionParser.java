package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.LastStep;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Transition;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.model.TransitionName;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Method;

/**
 * Parses {@link Transition} and {@link TestStep} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class TransitionParser implements AnnotationParser {
  private static Logger log = new Logger(TransitionParser.class);
  private String errors = "";

  @Override
  public String parse(ParserParameters parameters) {
    errors = "";
    String type = "";
    Object annotation = parameters.getAnnotation();
    String name = null;
    int weight = 0;
    if (annotation instanceof Transition) {
      Transition t = (Transition) annotation;
      name = t.name();
      //first we try the "name" property which dominates, then the default "value" property
      //since they both have default values of "" this is used as an indicator of undefined name
      //however, missing name is not taken as an error to allow leaving transitions unnamed if no guards or
      //oracles need to be associated to one
      if (name.length() == 0) {
        name = t.value();
      }
      weight = t.weight();
      type = Transition.class.getSimpleName();
    } else {
      TestStep ts = (TestStep) annotation;
      name = ts.name();
      //same as with transition tag above
      if (name.length() == 0) {
        name = ts.value();
      }
      weight = ts.weight();
      type = TestStep.class.getSimpleName();
    }
    TransitionName tName = checkName(name, parameters);
    if (tName == null) {
      return errors;
    }
    createTransition(parameters, tName, weight);

    Method method = parameters.getMethod();
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      errors += "@" + type + " methods are not allowed to have parameters: \"" + method.getName() + "()\" has " + parameterTypes.length + " parameters.\n";
    }

    return errors;
  }

  private TransitionName checkName(String name, ParserParameters parameters) {
    if (name.length() == 0) {
      errors += "Transition must have a name. Define the \"name\" or \"value\" property.\n";
      return null;
    }
    if (name.equals("all")) {
      errors += "Transition name \"all\" is reserved. Choose another.\n";
      return null;
    }
    String prefix = parameters.getPrefix();
    return new TransitionName(prefix, name);
  }

  private void createTransition(ParserParameters parameters, TransitionName name, int weight) {
    log.debug("creating transition:" + name);
    FSMTransition transition = parameters.getFsm().createTransition(name, weight);
    transition.setTransition(new InvocationTarget(parameters, Transition.class));
  }
}

