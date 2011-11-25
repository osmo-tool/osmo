package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
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

  @Override
  public String parse(ParserParameters parameters) {
    Transition t = (Transition) parameters.getAnnotation();
    String name = t.name();
    //first we try the "name" property which dominates, then the default "value" property
    //since they both have default values of "" this is used as an indicator of undefined name
    //however, missing name is not taken as an error to allow leaving transitions unnamed if no guards or
    //oracles need to be associated to one
    if (name.length() == 0) {
      name = t.value();
    }
    if (name.length() == 0) {
      return "Transition must have a name. Define the \"name\" or \"value\" property.";
    }
    if (name.equals("all")) {
      return "Transition name \"all\" is reserved. Choose another.\n";
    }
    String prefix = parameters.getPrefix();
    name = prefix + name;
    int weight = t.weight();
    log.debug("creating transition:"+name);
    FSMTransition transition = parameters.getFsm().createTransition(name, weight);
    transition.setTransition(new InvocationTarget(parameters, Transition.class));
    return "";
  }
}

