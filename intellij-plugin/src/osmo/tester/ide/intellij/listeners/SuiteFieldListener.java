package osmo.tester.ide.intellij.listeners;

import osmo.tester.ide.intellij.OSMORunParameters;
import osmo.tester.ide.intellij.endconditions.DefaultConfiguration;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** @author Teemu Kanstren */
public class SuiteFieldListener implements DocumentListener {
  private final OSMORunParameters parameters;
  private final JTextField suiteEndConditionTextField;
  private final JButton configureSECButton;

  public SuiteFieldListener(OSMORunParameters parameters, JTextField suiteEndConditionTextField, JButton configureSECButton) {
    this.parameters = parameters;
    this.suiteEndConditionTextField = suiteEndConditionTextField;
    this.configureSECButton = configureSECButton;
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
    String tec = suiteEndConditionTextField.getText();
    if (parameters.getSuiteConfigurationFor(tec) instanceof DefaultConfiguration) {
      configureSECButton.setEnabled(false);
    } else {
      configureSECButton.setEnabled(true);
    }
  }
}
