package osmo.miner.miner.program;

import osmo.miner.Config;
import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.log.Logger;
import osmo.miner.miner.Miner;
import osmo.miner.model.Node;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class ProgramModelMiner implements Miner {
  private static final Logger log = new Logger(ProgramModelMiner.class);
  private final Program mainProgram = new Program(null, "program");
  private Program currentProgram = mainProgram;

  public ProgramModelMiner() {
    Config.validate();
  }

  public Program getMainProgram() {
    return mainProgram;
  }

  @Override
  public void startElement(String element, Map<String, String> attributes) {
    if (element.equals(Config.variableId)) {
      //todo:make the program mining a tree where each subtree has its own variables
      //todo:and make that optional..?
      String name = attributes.get(Config.variableNameId);
      String value = attributes.get(Config.variableValueId);
      Variable var = currentProgram.createVariable(name);
      var.addValue(value);
    }
    if (element.equals(Config.stepId)) {
      String name = attributes.get(Config.stepNameId);
      log.debug("step start:"+name);
      currentProgram = currentProgram.createStep(name);
    }
  }

  @Override
  public void endElement(String element) {
    if (element.equals(Config.stepId)) {
      currentProgram = currentProgram.getParent();
      log.debug("Ending step:"+ currentProgram.getName());
    }
  }

  @Override
  public Node getRoot() {
    Node root = new Node(null, "root", new ArrayList<ValuePair>());
    addChildren(root, mainProgram);
    return root;
  }

  private void addChildren(Node node, Program program) {
    Map<String, Variable> variables = program.getVariables();
    List<ValuePair> pairs = new ArrayList<ValuePair>();
    for (Variable variable : variables.values()) {
      pairs.add(new ValuePair(variable.getName(), variable.getValues()));
    }
    node.addChild(program.getName(), pairs);
    //TODO: finish this
  }
}
