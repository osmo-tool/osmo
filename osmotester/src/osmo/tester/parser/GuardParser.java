package osmo.tester.parser;

import osmo.tester.annotation.Guard;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;

/**
 * Parses {@link Guard} annotations from the given model object.
 * 
 * @author Teemu Kanstren
 */
public class GuardParser implements AnnotationParser {
  private static Logger log = new Logger(GuardParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    Guard g = (Guard) parameters.getAnnotation();
    String[] transitionNames = g.value();
    for (String name : transitionNames) {
      FSM fsm = parameters.getFsm();
      InvocationTarget target = new InvocationTarget(parameters, Guard.class);
      if (name.equals("all")) {
        fsm.addGenericGuard(target);
        //generic guards should not be have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        //TODO: add check that no transition called "all" is allowed
        return "";
      }
      FSMTransition transition = fsm.createTransition(name);
      transition.addGuard(target);
    }
    return "";
  }
}
