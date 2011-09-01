package osmo.miner.prom;

import osmo.miner.Config;
import osmo.miner.log.Logger;
import osmo.miner.model.Node;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class ProgramMiner {
  private static final Logger log = new Logger(ProgramMiner.class);
  private final Program mainProgram;
  private Program currentProgram;

  public ProgramMiner(String name) {
    mainProgram = new Program(null, name);
    currentProgram = mainProgram;
    Config.validate();
  }

  public Program getMainProgram() {
    return mainProgram;
  }

  public void startElement(String element, Map<String, String> attributes) {
    if (element.equals(Config.variableId)) {
      String name = attributes.get(Config.variableNameId);
      String value = attributes.get(Config.variableValueId);
      Variable var = currentProgram.createVariable(name);
      var.addValue(value);
    }
    if (element.equals(Config.stepId)) {
      String name = attributes.get(Config.stepNameId);
//      log.debug("step start:"+name);
      currentProgram = currentProgram.createStep(name);
    }
  }

  public void endElement(String element) {
    if (element.equals(Config.stepId)) {
      currentProgram = currentProgram.getParent();
//      log.debug("Ending step:"+ currentProgram.getName());
    }
  }

}
