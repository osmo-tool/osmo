package osmo.miner.gui.attributetable;

import javax.swing.table.AbstractTableModel;

import osmo.miner.model.Node;

public class AttributeTableModel extends AbstractTableModel {
  private final Node node;

  public AttributeTableModel(Node node) {
    super();
    this.node = node;
  }

  @Override
  public String getColumnName(int column) {
    if (column == 0) {
      return "Name";
    }
    return "Value";
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public int getRowCount() {
    return node.getAttributes().size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    ValuePair pair = node.getAttributes().get(row);
    if (col == 0) {
      return pair.getName();
    }
    return pair.getValue();
  }
}
