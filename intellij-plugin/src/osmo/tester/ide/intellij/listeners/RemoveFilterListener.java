package osmo.tester.ide.intellij.listeners;

import osmo.tester.ide.intellij.OSMORunConfigEditor;
import osmo.tester.ide.intellij.OSMORunParameters;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** @author Teemu Kanstren */
public class RemoveFilterListener implements ActionListener {
  private final OSMORunParameters parameters;
  private final DefaultListModel<String> listModel;
  private final JList list;

  public RemoveFilterListener(JList list, OSMORunConfigEditor editor) {
    this.list = list;
    this.parameters = editor.getRunParameters();
    this.listModel = editor.getFiltersListModel();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object value = list.getSelectedValue();
    if (value == null) return;
    String selection = value.toString();
    parameters.removeFilter(selection);
    listModel.removeElement(selection);
  }
}
