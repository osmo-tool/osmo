package osmo.miner.gui.mainform;

import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AddButtonListener implements ActionListener {
  private final JFileChooser fc = new JFileChooser();
  private final MainForm parent;

  public AddButtonListener(MainForm parent) {
    super();
    this.parent = parent;
    fc.setMultiSelectionEnabled(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    fc.showOpenDialog(parent);
    File[] files = fc.getSelectedFiles();
    for (File file : files) {
      MOListModel listModel = parent.getListModel();
      listModel.add(new ModelObject(file));
    }
  }

}
