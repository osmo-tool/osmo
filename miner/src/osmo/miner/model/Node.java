package osmo.miner.model;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class Node implements TreeNode {
  private final Node parent;
  private final String name;
  private final ArrayList<Node> children = new ArrayList<Node>();

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

  @Override
  public String toString() {
    return name; // + "--" + children;
  }
}
