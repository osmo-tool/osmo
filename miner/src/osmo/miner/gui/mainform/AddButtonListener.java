package osmo.miner.gui.mainform;

import osmo.miner.model.HierarchyModel;
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
    InputStream in = null;
    try {
      File file = fc.getSelectedFiles()[0];
      DefaultListModel listModel = parent.getListModel();
      listModel.addElement(file);
      in = new FileInputStream(file);
    } catch (FileNotFoundException e1) {
      throw new RuntimeException("Failed to open file:", e1);
    }
    HierarchyModel model = parser.parse(parent.getRootNode(), in);
    parent.updateTree();
  }

}
