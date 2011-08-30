package osmo.miner.miner.program;

import osmo.miner.Config;
import osmo.miner.miner.Miner;
import osmo.miner.model.Node;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Variable;

import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class ProgramModelMiner implements Miner {
  private final Program program = new Program("program");
  private final Config config;

  public ProgramModelMiner(Config config) {
    this.config = config;
    config.validate();
  }

  public Program getProgram() {
    return program;
  }

  @Override
  public void startElement(String element, Map<String, String> attributes) {
    if (element.equals(config.getVariableId())) {
      //todo:make the program mining a tree where each subtree has its own variables
      //todo:and make that optional..?
      String name = attributes.get(config.getVariableNameId());
      String value = attributes.get(config.getVariableValueId());
      Variable var = program.createVariable(name);
      var.addValue(value);
    }
    if (element.equals(config.getStepId())) {
      String name = attributes.get(config.getStepNameId());
      Program subProgram = new Program(name);
    }
  }

  @Override
  public void endElement(String name) {
  }

  @Override
  public Node getRoot() {
    return null;
  }
}
