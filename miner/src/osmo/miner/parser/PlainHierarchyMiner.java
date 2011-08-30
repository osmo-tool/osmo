package osmo.miner.parser;

import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.model.Node;

import java.util.ArrayList;
import java.util.List;

public class PlainHierarchyMiner implements Miner {
  private Node root = new Node(null, "root", new ArrayList<ValuePair>());
  private Node current = null;

  public PlainHierarchyMiner() {
    current = root;
  }

  @Override
  public void startElement(String name, List<ValuePair> attributes) {
    current = current.addChild(name, attributes);
  }

  @Override
  public void endElement(String name) {
    current = current.getParent();
  }

  public Node getRoot() {
    return root;
  }
}
