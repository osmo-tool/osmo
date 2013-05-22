package osmo.tester.ide.intellij;

import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.ui.AlternativeJREPanel;
import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;

/** @author Teemu Kanstren */
public class OSMORunConfigEditor extends SettingsEditor<OSMORunConfig> {
  private JPanel topPanel;
  private JTextField seedTextField;
  private JRadioButton classesRadioButton;
  private JRadioButton factoryRadioButton;
  private JTabbedPane tabbedPane;
  private JRadioButton packageRadioButton;
  private JTextField testEndConditionTextField;
  private JButton button1;
  private JTextField suiteEndConditionTextField;
  private JButton button2;
  private JButton configureButton;
  private JButton configureButton1;
  private JButton randomizeButton;
  private JPanel jdkSettingsTab;
  private JPanel listenersTab;
  private JPanel filtersTab;
  private JPanel jdkSettingsPanel;
  private JComboBox moduleComboBox;
  private JPanel jdkBottomPanel;
  private JPanel jdkBottom2;
  private JCheckBox stopOnErrorCheckBox;
  private JCheckBox unwrapExceptionsCheckBox;
  private JPanel modelObjectPanel;
  private JTextField modelObjectField;
  private JLabel modelObjectLabel;
  private JButton modelObjectButton;
  private JButton addClassButton;
  private JButton removeClassButton;
  private JList classesList;
  private JList listenersList;
  private JButton addListenerButton;
  private JButton removeListenerButton;
  private JList filterList;
  private JButton addFilterButton;
  private JButton removeFilterButton;
  private final CommonJavaParametersPanel commonJavaParametersPanel = new CommonJavaParametersPanel();
  //this gives the "use alternative JDK selection"
  private AlternativeJREPanel alternateJDK = new AlternativeJREPanel();
  private final ConfigurationModuleSelector moduleSelector;

  public OSMORunConfigEditor(Project project) {
    jdkSettingsPanel.add(commonJavaParametersPanel, BorderLayout.CENTER);
    jdkSettingsPanel.add(jdkBottomPanel, BorderLayout.SOUTH);
    alternateJDK.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
//    jdkBottomPanel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
    jdkBottomPanel.add(alternateJDK, BorderLayout.SOUTH);
    moduleSelector = new ConfigurationModuleSelector(project, moduleComboBox);
    packageRadioButton.addChangeListener(new ModelObjectListener(this, "Package:", true));
    factoryRadioButton.addChangeListener(new ModelObjectListener(this, "Factory:", true));
    classesRadioButton.addChangeListener(new ModelObjectListener(this, "Use the 'Classes' tab below to set the model classes.", false));

    addClassButton.setIcon(Resources.PLUS_ICON);
    addClassButton.setText("");
    removeClassButton.setIcon(Resources.MINUS_ICON);
    removeClassButton.setText("");

    addListenerButton.setIcon(Resources.PLUS_ICON);
    addListenerButton.setText("");
    removeListenerButton.setIcon(Resources.MINUS_ICON);
    removeListenerButton.setText("");

    addFilterButton.setIcon(Resources.PLUS_ICON);
    addFilterButton.setText("");
    removeFilterButton.setIcon(Resources.MINUS_ICON);
    removeFilterButton.setText("");
  }

  public JTextField getModelObjectField() {
    return modelObjectField;
  }

  public JLabel getModelObjectLabel() {
    return modelObjectLabel;
  }

  public JButton getModelObjectButton() {
    return modelObjectButton;
  }

  public JTabbedPane getTabbedPane() {
    return tabbedPane;
  }

  @Override
  protected void resetEditorFrom(OSMORunConfig s) {
    //this fills in the combobox for module selections
    moduleSelector.reset(s);
  }

  @Override
  protected void applyEditorTo(OSMORunConfig s) throws ConfigurationException {
    OSMORunParameters parameters = s.getRunParameters();
    commonJavaParametersPanel.applyTo(parameters);
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return topPanel;
  }

  @Override
  protected void disposeEditor() {
  }
}
