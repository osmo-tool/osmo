package osmo.tester.testmodels.transformation;

import java.util.ArrayList;
import java.util.Collection;

/** @author Teemu Kanstren */
public class TreeNode {
  private final String name;
  private TreeNode parent = null;
  private Collection<TreeNode> children = new ArrayList<>();
  private Collection<TreeLeaf> leaves = new ArrayList<>();

  public TreeNode(String name) {
    this.name = name;
  }

  public TreeNode addChildNode(String name) {
    TreeNode child = new TreeNode(name);
    child.parent = this;
    children.add(child);
    return child;
  }

  public void addLeaf(String name, int size) {
    leaves.add(new TreeLeaf(name, size));
  }

  public TreeNode getParent() {
    return parent;
  }

  public Collection<TreeNode> getChildren() {
    return children;
  }

  public Collection<TreeLeaf> getLeaves() {
    return leaves;
  }

  public boolean hasGrandChildren() {
    for (TreeNode child : children) {
      if (child.children.size() > 0) {
        return true;
      }
    }
    return false;
  }

  public String getName() {
    return name;
  }
}
