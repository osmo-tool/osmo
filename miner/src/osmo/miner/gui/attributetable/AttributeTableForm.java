package osmo.miner.gui.attributetable;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JTable;

import osmo.miner.model.Node;

public class AttributeTableForm extends JFrame {
  private JTable table;
  
  public AttributeTableForm(Node node) {
    setTitle("Attributes for "+node.getName());
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
