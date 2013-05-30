package osmo.tester.ide.intellij.listeners;

import osmo.tester.ide.intellij.OSMORunParameters;
import osmo.tester.ide.intellij.endconditions.EndConditions;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** @author Teemu Kanstren */
public class TestFieldListener implements DocumentListener {
  private final OSMORunParameters parameters;
  private final JTextField testEndConditionTextField;
  private final JButton configureTECButton;

  public TestFieldListener(OSMORunParameters parameters, JTextField testEndConditionTextField, JButton configureTECButton) {
    this.parameters = parameters;
    this.testEndConditionTextField = testEndConditionTextField;
    this.configureTECButton = configureTECButton;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    check();
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    check();
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    check();
  }

  private void check() {
    String tec = testEndConditionTextField.getText();
    if (EndConditions.getConfigurationFor(tec) == null) {
      configureTECButton.setEnabled(false);
    } else {
      configureTECButton.setEnabled(true);
    }
  }
}
