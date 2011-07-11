package osmo.tester.parser;

import osmo.tester.annotation.Transition;
import osmo.tester.log.Logger;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;

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
    int weight = t.weight();
    FSMTransition transition = parameters.getFsm().createTransition(name, weight);
    transition.setTransition(new InvocationTarget(parameters, Transition.class));
    return "";
  }
}

