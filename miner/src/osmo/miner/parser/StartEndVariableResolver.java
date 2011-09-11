package osmo.miner.parser;

import osmo.miner.Config;
import osmo.miner.log.Logger;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Step;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class StartEndVariableResolver {
  private static final Logger log = new Logger(StartEndVariableResolver.class);

  public void resolve(Program program) {
    Map<String,String> variables = program.getVariables();
    List<Step> steps = program.getSteps();
    log.debug("Resolving program:" + program.getName());
    resolve(variables, steps, new HashMap<String, String>());
  }

  public void resolve(Map<String, String> toResolve, List<Step> steps, Map<String, String> parent) {
//    log.debug("variables:"+variables);
    for (Step step : steps) {
//      log.debug("Resolving step:"+step.getName());
      resolve(step.getVariables(), step.getSubSteps(), toResolve);
    }
    for (Map.Entry<String, String> entry : toResolve.entrySet()) {
      String name = entry.getKey();
      String value = entry.getValue();
      if (value == null) {
        value = "UNDEFINED( ? )";
      }
//      log.debug("Name:"+name+" value:"+value);
      String pre = Config.variablePre;
      String post = Config.variablePost;
      if (value.startsWith(pre) && value.endsWith(post)) {
        String key = value.substring(pre.length(), value.length()-post.length());
//        log.debug("Variable reference:"+key);
        String stepValue = toResolve.get(key);
        if (stepValue != null) {
          toResolve.put(name, stepValue);
//          log.debug("Set value:" + stepValue);
        }
        stepValue = parent.get(key);
        if (stepValue != null) {
          toResolve.put(name, stepValue);
//          log.debug("Set value:" + stepValue);
        }
      }

//        log.debug("key:"+key);
/*        for (Step step : steps) {
          Map<String, String> stepVariables = step.getVariables();
        }*/
    }
  }
}
