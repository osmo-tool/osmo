package osmo.tester.testmodels.transformation;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.BeforeTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.Post;
import osmo.tester.annotation.TestStep;
import osmo.tester.annotation.TestSuiteField;
import osmo.tester.annotation.Variable;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.ValueSet;

/** @author Teemu Kanstren */
public class TransformationModel {
  public TreeNode root = null;
  private TreeNode current = null;
  @Variable
  private int depth = 0;
  private int deepest = 0;
  @Variable
  private int branch = 1;
  private int nextLeafId = 1;
  @Variable("sizes")
  private final ValueSet<Integer> sizes = new ValueSet<>(1, 2, 3);
  @Variable
  private int leafCount = 0;
  @Variable
  private int nodeCount = 0;
  @TestSuiteField
  private TestSuite suite = null;

  @BeforeTest
  public void startTest() {
    this.root = new TreeNode("root");
    suite.getCurrentTest().setAttribute("root", root);
    this.current = root;
    depth = 0;
    deepest = 0;
    branch = 1;
    nextLeafId = 1;
    leafCount = 0;
    nodeCount = 0;
  }

  /** Create a new child node under current node */
  @TestStep("create-node")
  public void createNode() {
    current = current.addChildNode("node" + branch + "_" + depth);
    depth++;
    if (depth > deepest) {
      deepest = depth;
    }
    if (current.getName().equals("root")) {
      branch++;
    }
  }

  @Post
  public void updateState() {
    leafCount = current.getLeaves().size();
    nodeCount = current.getChildren().size();
  }

  /** Create a leaf on the current node */
  @TestStep("create-leaf")
  public void createLeaf() {
    current.addLeaf("leaf" + nextLeafId, sizes.next());
    nextLeafId++;
  }

  @Guard("up")
  public boolean allowUp() {
    return current.getParent() != null;
  }

  @TestStep("up")
  public void up() {
    current = current.getParent();
    branch++;
  }

  @AfterTest
  public void afterAll() {
//    System.out.println("deppest:"+deepest);
    suite.addValue("deepest", deepest);
    suite.addValue("width", branch);
  }
}
