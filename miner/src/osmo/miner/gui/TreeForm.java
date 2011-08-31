package osmo.miner.gui;

import osmo.miner.gui.attributetable.ValuePair;
import osmo.miner.gui.mainform.ModelObject;
import osmo.miner.miner.Miner;
import osmo.miner.model.Node;
import osmo.miner.parser.Parser;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Teemu Kanstren
 */
public abstract class TreeForm extends JPanel {
  private JTree tree;
  private Node rootNode = new Node(null, "root", new ArrayList<ValuePair>());
  private final Map<ModelObject, Node> roots = new HashMap<ModelObject, Node>();
  private final Parser parser;

  public TreeForm() {
    tree = new JTree(rootNode);
    tree.addMouseListener(new TreeMouseListener(tree));
    DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
    renderer.setOpenIcon(null);
    renderer.setClosedIcon(null);
    JScrollPane scrollPane = new JScrollPane();
    setLayout(new BorderLayout());
    add(scrollPane, BorderLayout.CENTER);
    scrollPane.setViewportView(tree);
    parser = createParser();
  }

  public abstract Parser createParser();

  public abstract Miner createMiner();

  public synchronized Node transform(ModelObject newRoot) {
    Node root = roots.get(newRoot);
    if (root == null) {
      Miner miner = createMiner();
      parser.addMiner(miner);
      parser.parse(newRoot.getInputStream());
      root = miner.getRoot();
      roots.put(newRoot, root);
    }
    return root;
  }

  public void updateWith(ModelObject mo) {
    Node root = transform(mo);
    root.cloneTo(this.rootNode);
    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    model.reload();
  }
}
