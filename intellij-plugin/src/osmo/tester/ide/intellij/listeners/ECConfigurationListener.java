package osmo.tester.ide.intellij.listeners;

import com.intellij.openapi.project.Project;
import osmo.tester.ide.intellij.OSMORunParameters;
import osmo.tester.ide.intellij.endconditions.EndConditions;

import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** @author Teemu Kanstren */
public class ECConfigurationListener implements ActionListener {
  private Project project;
  private JTextField ecTextField;
  private OSMORunParameters parameters;
  private boolean test;

  public ECConfigurationListener(Project project, JTextField ecTextField, OSMORunParameters parameters, boolean test) {
    this.project = project;
    this.ecTextField = ecTextField;
    this.parameters = parameters;
    this.test = test;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (test) {
      EndConditions.getConfigurationFor(ecTextField.getText()).showGUI(project);
    } else {
      EndConditions.getConfigurationFor(ecTextField.getText()).showGUI(project);
    }
  }
}
