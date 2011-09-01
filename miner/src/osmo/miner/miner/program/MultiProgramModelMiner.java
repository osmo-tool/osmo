package osmo.miner.miner.program;

import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.gui.mainform.ModelObject;
import osmo.miner.model.Node;
import osmo.miner.model.program.Program;
import osmo.miner.model.program.Variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public class MultiProgramModelMiner {
  public void mine() {
  }

  public Node nodeFor(Collection<Program> programs) {
    
    return null;
  }

  public Node getRoot() {
    Node root = new Node(null, "Model", new ArrayList<ValuePair>());
    createVariableNode(root);
    addChildren(root, mainProgram);
    return root;
  }

  private void createVariableNode(Node root) {
    Map<String, Variable> variables = mainProgram.getGlobalVariables();
    Node variableNode = root.addChild("Variables", new ArrayList<ValuePair>());
    for (Variable variable : variables.values()) {
      List<ValuePair> pairs = new ArrayList<ValuePair>();
      pairs.add(new ValuePair("Values", variable.getValues().toString()));
      variableNode.addChild(variable.getName(), pairs);
    }
  }

  private List<ValuePair> parameters(Program program) {
    Map<String, Variable> variables = program.getVariables();
    List<ValuePair> pairs = new ArrayList<ValuePair>();
    for (Variable variable : variables.values()) {
      pairs.add(new ValuePair(variable.getName(), variable.getValues()));
    }
    return pairs;
  }

  private void addChildren(Node node, Program program) {
    Map<String, Program> steps = program.getSteps();
    for (Program subProgram : steps.values()) {
      Node child = node.addChild(subProgram.getName(), parameters(subProgram));
      addChildren(child, subProgram);
    }
  }
}
