package osmo.miner.gui.mainform;

import osmo.miner.log.Logger;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Teemu Kanstren
 */
public class MOSelectionListener implements ListSelectionListener {
  private static final Logger log = new Logger(MOSelectionListener.class);
  private final MainForm parent;

  public MOSelectionListener(MainForm parent) {
    this.parent = parent;
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
//      return;
    }
    JList list = (JList) e.getSource();
    MOListModel model = (MOListModel) list.getModel();
    int index = list.getSelectedIndex();
    log.debug("index:"+index);
    ModelObject mo = (ModelObject) model.getElementAt(index);
    parent.updateSelection(mo);
  }
}
