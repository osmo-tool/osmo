package osmo.miner.gui.attributetable;

import osmo.miner.model.Node;

import javax.swing.JTable;

public class AttributeTable extends JTable {

  public AttributeTable(Node node) {
    super();
    setModel(new AttributeTableModel(node));
  }
}
