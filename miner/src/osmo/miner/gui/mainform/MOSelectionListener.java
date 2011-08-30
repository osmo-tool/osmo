package osmo.miner.gui.mainform;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Teemu Kanstren
 */
public class MOSelectionListener implements ListSelectionListener {
  private final MainForm parent;

  public MOSelectionListener(MainForm parent) {
    this.parent = parent;
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }
    JList list = (JList) e.getSource();
    MOListModel model = (MOListModel) list.getModel();
    int index = e.getFirstIndex();
    ModelObject mo = (ModelObject) model.getElementAt(index);
    parent.updateSelection(mo);
  }
}
