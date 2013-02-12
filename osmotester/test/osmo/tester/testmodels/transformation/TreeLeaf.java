package osmo.tester.testmodels.transformation;

/** @author Teemu Kanstren */
public class TreeLeaf {
  private final String name;
  private final int size;

  public TreeLeaf(String name, int size) {
    this.name = name;
    this.size = size;
  }

  public int getSize() {
    return size;
  }

  @Override
  public String toString() {
    return name + "_" + size + "";
  }
}
