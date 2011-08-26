package osmo.miner.model;

import java.util.ArrayList;
import java.util.List;

public class Node {
  private final Node parent;
  private final String name;
  private final List<Node> children = new ArrayList<Node>();

  public Node(Node parent, String name) {
    super();
    this.parent = parent;
    this.name = name;
  }

  public Node addChild(String name) {
    Node child = new Node(this, name);
    children.add(child);
    return child;
  }

  public List<Node> getChildren() {
    return children;
  }

  public String getName() {
    return name;
  }

  public Node getParent() {
    return parent;
  }

  @Override
  public String toString() {
    return name + "--" + children;
  }

}
