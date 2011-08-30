package osmo.miner.gui.attributetable;

import osmo.miner.model.Node;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;

public class AttributeTableForm extends JFrame {
  private JTable table;

  public AttributeTableForm(Node node) {
    setTitle("Attributes for " + node.getName());
//    setSize(800, 600);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    JScrollPane scrollPane = new JScrollPane();
    getContentPane().add(scrollPane, BorderLayout.CENTER);

    table = new AttributeTable(node);
    scrollPane.setViewportView(table);
    pack();
    setVisible(true);
  }

}
