package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.Transition;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

/**
 * Parses {@link Transition} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class TransitionParser implements AnnotationParser {
  private static Logger log = new Logger(TransitionParser.class);
  private String errors = "";

  @Override
  public String parse(ParserParameters parameters) {
    errors = "";
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
    } else {
      TestStep ts = (TestStep) annotation;
      name = ts.name();
      //same as with transition tag above
      if (name.length() == 0) {
        name = ts.value();
      }
      weight = ts.weight();
    }
    name = checkName(name, parameters);
    if (name == null) {
      return errors;
    }
    createTransition(parameters, name, weight);
    return "";
  }

  private String checkName(String name, ParserParameters parameters) {
    if (name.length() == 0) {
      errors = "Transition must have a name. Define the \"name\" or \"value\" property.";
      return null;
    }
    if (name.equals("all")) {
      errors = "Transition name \"all\" is reserved. Choose another.\n";
      return null;
    }
    String prefix = parameters.getPrefix();
    name = prefix + name;
    return name;
  }

  private void createTransition(ParserParameters parameters, String name, int weight) {
    log.debug("creating transition:" + name);
    FSMTransition transition = parameters.getFsm().createTransition(name, weight);
    transition.setTransition(new InvocationTarget(parameters, Transition.class));
  }
}

