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
    String name = t.value();
    FSMTransition transition = parameters.getFsm().createTransition(name);
    transition.setTransition(new InvocationTarget(parameters, Transition.class));
    return "";
  }
}

