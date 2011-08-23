package osmo.miner.gui.mainform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

public class AddButtonListener implements ActionListener {
  private final JFileChooser fc = new JFileChooser();
  private final MainForm parent;
  
  public AddButtonListener(MainForm parent) {
    super();
    this.parent = parent;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    fc.showOpenDialog(parent);
  }

}
