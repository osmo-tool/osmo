package osmo.miner.testminer.model;

import osmo.common.ValuePair;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class Node implements TreeNode {
  private final Node parent;
  private final String name;
  private ArrayList<Node> children = new ArrayList<Node>();
  private List<ValuePair> attributes;

  public Node(Node parent, String name, List<ValuePair> attributes) {
    super();
    this.parent = parent;
    this.name = name;
    this.attributes = attributes;
  }

  public Node addChild(String name, List<ValuePair> attributes) {
    Node child = new Node(this, name, attributes);
    children.add(child);
    return child;
  }

  public List<Node> getChildren() {
    return children;
  }

  public List<ValuePair> getAttributes() {
    return attributes;
  }

  public String getName() {
    return name;
  }

  public Node getParent() {
    return parent;
  }

  @Override
  public TreeNode getChildAt(int i) {
    return children.get(i);
  }

  @Override
  public int getChildCount() {
    return children.size();
  }

  @Override
  public int getIndex(TreeNode node) {
    return children.indexOf(node);
  }

  @Override
  public boolean getAllowsChildren() {
    return true;
  }

  @Override
  public boolean isLeaf() {
    return false;
  }

  @Override
  public Enumeration children() {
    Enumeration e = new Enumeration() {
      Iterator i = children.iterator();

      @Override
      public boolean hasMoreElements() {
        return i.hasNext();
      }

      @Override
      public Object nextElement() {
        return i.next();
      }
    };
    return e;
  }

  public void cloneTo(Node target) {
    target.children = children;
    target.attributes = attributes;
  }

  @Override
  public String toString() {
    return name; // + "--" + children;
  }

  public String treeString() {
    // System.out.println("root:" + current);
    StringBuilder sb = new StringBuilder();
    appendNode(this, sb);
    appendChildren(this, sb, 1);
    return sb.toString();
  }

  private void appendChildren(Node node, StringBuilder sb, int depth) {
    for (Node child : node.children) {
      sb.append("\n");
      for (int i = 0; i < depth; i++) {
        sb.append("--");
      }
      // System.out.println("Appending:" + child);
      appendNode(child, sb);
      appendChildren(child, sb, depth + 1);
    }
  }

  private void appendNode(Node node, StringBuilder sb) {
    sb.append(node.getName());
    for (ValuePair attribute : node.attributes) {
      sb.append(":");
      sb.append(attribute.getValue1());
      sb.append("=");
      sb.append(attribute.getValue2());
    }
  }

}
