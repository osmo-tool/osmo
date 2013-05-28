package osmo.tester.ide.intellij.listeners;

import osmo.tester.ide.intellij.OSMORunParameters;
import osmo.tester.ide.intellij.endconditions.DefaultConfiguration;

import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** @author Teemu Kanstren */
public class FactoryFieldListener implements DocumentListener {
  private final OSMORunParameters parameters;
  private final JTextField field;

  public FactoryFieldListener(OSMORunParameters parameters, JTextField field) {
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
    parameters.setRunClass(field.getText());
  }
}
