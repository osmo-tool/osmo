package osmo.tester.parser;

import osmo.tester.annotation.Oracle;
import osmo.tester.log.Logger;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;
import osmo.tester.model.InvocationTarget;

import java.lang.reflect.Method;

/**
 * Parses {@link osmo.tester.annotation.Oracle} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class OracleParser implements AnnotationParser {
  private static Logger log = new Logger(OracleParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    Oracle oracle = (Oracle) parameters.getAnnotation();

    Method method = parameters.getMethod();
    String errors = "";
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      //TODO: add warnings for wrong return types etc.
      errors += "Oracle methods are not allowed to have parameters: \""+method.getName()+"()\" has "+parameterTypes.length+" parameters.\n";
    }

    InvocationTarget target = new InvocationTarget(parameters, Oracle.class);
    FSM fsm = parameters.getFsm();
    String[] transitionNames = oracle.value();
    for (String name : transitionNames) {
      log.debug("Parsing oracle '"+name+"'");
      //todo: add test for transition named "all" in a test model
      if (name.equals("all")) {
        fsm.addGenericOracle(target);
        //generic guards should not be have their own transition or it will fail the FSM check since it is a guard
        //without a transition
        continue;
      }
      FSMTransition transition = fsm.createTransition(name, -1);
      transition.addOracle(target);
    }
    return errors;
  }
}
