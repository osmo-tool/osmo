package osmo.miner.gui.attributetable;

import javax.swing.JTable;

import osmo.miner.model.Node;

public class AttributeTable extends JTable {

  public AttributeTable(Node node) {
    super();
    setModel(new AttributeTableModel(node));
  }
}
