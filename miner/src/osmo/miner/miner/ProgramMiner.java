package osmo.miner.miner;

import osmo.miner.Config;
import osmo.miner.log.Logger;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Step;
import osmo.miner.model.program.Variable;

import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class ProgramMiner {
  private static final Logger log = new Logger(ProgramMiner.class);
  private final Program program;
  private Step step;

  public ProgramMiner(String name) {
    program = new Program(name);
    Config.validate();
  }

  public Program getProgram() {
    return program;
  }

  public void startElement(String element, Map<String, String> attributes) {
    if (element.equals(Config.variableId)) {
      String name = attributes.get(Config.variableNameId);
      String value = attributes.get(Config.variableValueId);
      Variable var = null;
      if (step != null) {
        var = step.createVariable(name);
      } else {
        var = program.createVariable(name);
      }
      var.addValue(value);
    }
    if (element.equals(Config.stepId)) {
      String name = attributes.get(Config.stepNameId);
//      log.debug("step start:"+name);
      step = program.createStep(name);
    }
  }

  public void endElement(String element) {
    if (element.equals(Config.stepId)) {
      step = null;
//      log.debug("Ending step:"+ currentProgram.getName());
    }
  }

}
