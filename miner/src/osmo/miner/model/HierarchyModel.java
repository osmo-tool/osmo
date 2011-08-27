package osmo.miner.model;

public class HierarchyModel {
  private Node current = null;

  public HierarchyModel(Node current) {
    this.current = current;
  }

  public void add(String name) {
    current = current.addChild(name);
  }

  public void up() {
    current = current.getParent();
  }

  @Override
  public String toString() {
    // System.out.println("root:" + current);
    StringBuilder sb = new StringBuilder();
    sb.append(current.getName());
    appendChildren(current, sb, 1);
    return sb.toString();
  }

  private void appendChildren(Node node, StringBuilder sb, int depth) {
    for (Node child : node.getChildren()) {
      sb.append("\n");
      for (int i = 0; i < depth; i++) {
        sb.append("--");
      }
      // System.out.println("Appending:" + child);
      sb.append(child.getName());
      appendChildren(child, sb, depth+1);
    }
  }
}
