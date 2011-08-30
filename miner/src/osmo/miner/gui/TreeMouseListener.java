package osmo.miner.gui;

import osmo.miner.gui.attributetable.AttributeTableForm;
import osmo.miner.model.Node;

import javax.swing.JTree;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Teemu Kanstren
 */
public class TreeMouseListener extends MouseAdapter {
  private final JTree tree;

  public TreeMouseListener(JTree tree) {
    this.tree = tree;
  }

  public void mousePressed(MouseEvent e) {
    if (e.getButton() != MouseEvent.BUTTON3) {
      return;
    }
    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
    Node node = (Node) path.getLastPathComponent();
    System.out.println("clicked on:" + node);
    AttributeTableForm form = new AttributeTableForm(node);
  }
}
