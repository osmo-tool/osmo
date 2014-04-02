package osmo.tester.ide.intellij.listeners;

import osmo.tester.ide.intellij.OSMORunParameters;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** @author Teemu Kanstren */
public class PackageFieldListener implements DocumentListener {
  private final OSMORunParameters parameters;
  private final JTextField field;

  public PackageFieldListener(OSMORunParameters parameters, JTextField field) {
    this.parameters = parameters;
    this.field = field;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    store();
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    store();
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    store();
  }

  private void store() {
    parameters.setPackageName(field.getText());
  }
}
