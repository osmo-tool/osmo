package osmo.tester.parser;

import osmo.tester.annotation.Oracle;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;

/**
 * @author Teemu Kanstren
 */
public class OracleParser implements AnnotationParser {
  private static Logger log = new Logger(AfterParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    Oracle oracle = (Oracle) parameters.getAnnotation();
    InvocationTarget target = new InvocationTarget(parameters, Oracle.class);
    FSM fsm = parameters.getFsm();
    String[] transitionNames = oracle.value();
    for (String name : transitionNames) {
      log.debug("Parsing oracle '"+name+"'");
      if (name.equals("all")) {
        fsm.addGenericOracle(target);
        //generic guards should not be have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        //TODO: add check that no transition called "all" is allowed
        continue;
      }
      FSMTransition transition = fsm.createTransition(name, -1);
      transition.addOracle(target);
    }
    return "";
  }
}
