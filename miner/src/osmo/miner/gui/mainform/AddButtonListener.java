package osmo.miner.gui.mainform;

import osmo.miner.model.ModelObject;
import osmo.miner.model.Node;
import osmo.miner.parser.XmlParser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.*;

public class AddButtonListener implements ActionListener {
  private final JFileChooser fc = new JFileChooser();
  private final MainForm parent;
  private XmlParser parser = new XmlParser();

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
