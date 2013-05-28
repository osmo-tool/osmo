package osmo.tester.ide.intellij;

import com.intellij.execution.ui.AlternativeJREPanel;
import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.NotNull;
import osmo.tester.ide.intellij.filters.LongOnlyDocument;
import osmo.tester.ide.intellij.listeners.AddClassListener;
import osmo.tester.ide.intellij.listeners.AddFilterListener;
import osmo.tester.ide.intellij.listeners.AddListenerListener;
import osmo.tester.ide.intellij.listeners.AlgorithmChoiceListener;
import osmo.tester.ide.intellij.listeners.ECChoiceListener;
import osmo.tester.ide.intellij.listeners.ECConfigurationListener;
import osmo.tester.ide.intellij.listeners.FactoryFieldListener;
import osmo.tester.ide.intellij.listeners.ModelObjectListener;
import osmo.tester.ide.intellij.listeners.PackageFactoryListener;
import osmo.tester.ide.intellij.listeners.PackageFieldListener;
import osmo.tester.ide.intellij.listeners.RandomizeSeedListener;
import osmo.tester.ide.intellij.listeners.RemoveClassListener;
import osmo.tester.ide.intellij.listeners.RemoveFilterListener;
import osmo.tester.ide.intellij.listeners.RemoveListenerListener;
import osmo.tester.ide.intellij.listeners.SuiteFieldListener;
import osmo.tester.ide.intellij.listeners.TestFieldListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
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
import java.awt.BorderLayout;

/** @author Teemu Kanstren */
public class OSMORunConfigEditor extends SettingsEditor<OSMORunConfig> {
  private JPanel topPanel;
  private JTextField seedTextField;
  private JRadioButton classesRadioButton;
  private JRadioButton factoryRadioButton;
  private JTabbedPane tabbedPane;
  private JRadioButton packageRadioButton;
  private JTextField testEndConditionTextField;
  private JButton testECChoiceButton;
  private JTextField suiteEndConditionTextField;
  private JButton suiteECChoiceButton;
  private JButton configureTECButton;
  private JButton configureSECButton;
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
  private JTextField factoryField;
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
  private JLabel modelObjectLabel2;
  private JTextField algorithmField;
  private JButton algorithmButton;
  private JTextField packageField;
  private DefaultListModel<String> classesModel = new DefaultListModel<>();
  private DefaultListModel<String> filtersModel = new DefaultListModel<>();
  private DefaultListModel<String> listenersModel = new DefaultListModel<>();
  private final CommonJavaParametersPanel commonJavaParametersPanel = new CommonJavaParametersPanel();
  //this gives the "use alternative JDK selection"
  private AlternativeJREPanel alternateJDK = new AlternativeJREPanel();
  private final ConfigurationModuleSelector moduleSelector;
  private final OSMORunParameters parameters = new OSMORunParameters();

  public OSMORunConfigEditor(Project project) {
    jdkSettingsPanel.add(commonJavaParametersPanel, BorderLayout.CENTER);
    jdkSettingsPanel.add(jdkBottomPanel, BorderLayout.SOUTH);
    alternateJDK.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
//    jdkBottomPanel.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
    jdkBottomPanel.add(alternateJDK, BorderLayout.SOUTH);
    moduleSelector = new ConfigurationModuleSelector(project, moduleComboBox);
    packageRadioButton.addChangeListener(new ModelObjectListener(this, ModelObjectListener.PACKAGE));
    factoryRadioButton.addChangeListener(new ModelObjectListener(this, ModelObjectListener.FACTORY));
    classesRadioButton.addChangeListener(new ModelObjectListener(this, ModelObjectListener.CLASSES));

    addClassButton.setIcon(Resources.PLUS_ICON);
    addClassButton.setText("");
    addClassButton.addActionListener(new AddClassListener(project, this));

    removeClassButton.setIcon(Resources.MINUS_ICON);
    removeClassButton.setText("");
    removeClassButton.addActionListener(new RemoveClassListener(classesList, this));
    
    classesList.setModel(classesModel);

    addListenerButton.setIcon(Resources.PLUS_ICON);
    addListenerButton.setText("");
    addListenerButton.addActionListener(new AddListenerListener(project, this));
    
    removeListenerButton.setIcon(Resources.MINUS_ICON);
    removeListenerButton.setText("");
    removeListenerButton.addActionListener(new RemoveListenerListener(listenersList, this));
    
    listenersList.setModel(listenersModel);

    addFilterButton.setIcon(Resources.PLUS_ICON);
    addFilterButton.setText("");
    addFilterButton.addActionListener(new AddFilterListener(project, this));

    removeFilterButton.setIcon(Resources.MINUS_ICON);
    removeFilterButton.setText("");
    removeFilterButton.addActionListener(new RemoveFilterListener(filterList, this));

    filterList.setModel(filtersModel);
    
    algorithmButton.addActionListener(new AlgorithmChoiceListener(project, algorithmField, moduleSelector));

    TestFieldListener testFieldListener = new TestFieldListener(parameters, testEndConditionTextField, configureTECButton);
    testEndConditionTextField.getDocument().addDocumentListener(testFieldListener);

    SuiteFieldListener suiteFieldListener = new SuiteFieldListener(parameters, suiteEndConditionTextField, configureSECButton);
    suiteEndConditionTextField.getDocument().addDocumentListener(suiteFieldListener);
    
    configureTECButton.addActionListener(new ECConfigurationListener(project, testEndConditionTextField, parameters, true));
    configureSECButton.addActionListener(new ECConfigurationListener(project, suiteEndConditionTextField, parameters, false));
    
    testECChoiceButton.addActionListener(new ECChoiceListener(project, "Choose Test End Condition", testEndConditionTextField, moduleSelector));
    suiteECChoiceButton.addActionListener(new ECChoiceListener(project, "Choose Suite End Condition", suiteEndConditionTextField, moduleSelector));
    
    randomizeButton.addActionListener(new RandomizeSeedListener(seedTextField));
    seedTextField.setDocument(new LongOnlyDocument());
    factoryField.getDocument().addDocumentListener(new FactoryFieldListener(parameters, factoryField));
    packageField.getDocument().addDocumentListener(new PackageFieldListener(parameters, packageField));

    GridLayoutManager layout = (GridLayoutManager) modelObjectPanel.getLayout();
    layout.getConstraintsForComponent(modelObjectLabel2).setColSpan(2);
    
    classesRadioButton.setSelected(true);
    
    modelObjectButton.addActionListener(new PackageFactoryListener(this, project, factoryRadioButton));
  }

  public DefaultListModel<String> getClassesListModel() {
    return classesModel;
  }

  public DefaultListModel<String> getFiltersListModel() {
    return filtersModel;
  }

  public DefaultListModel<String> getListenersListModel() {
    return listenersModel;
  }

  public JTextField getFactoryField() {
    return factoryField;
  }

  public JLabel getModelObjectLabel() {
    return modelObjectLabel;
  }

  public JLabel getModelObjectLabel2() {
    return modelObjectLabel2;
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
    OSMORunParameters parameters = s.getRunParameters();
    commonJavaParametersPanel.reset(parameters);
    stopOnErrorCheckBox.setSelected(parameters.isStopOnError());
    unwrapExceptionsCheckBox.setSelected(parameters.isUnWrapExceptions());
    algorithmField.setText(parameters.getAlgorithm());
    testEndConditionTextField.setText(parameters.getTestEndCondition().get(OSMORunParameters.KEY_EC_CLASS_NAME));
    suiteEndConditionTextField.setText(parameters.getSuiteEndCondition().get(OSMORunParameters.KEY_EC_CLASS_NAME));
    seedTextField.setText(""+parameters.getSeed());
    packageField.setText(parameters.getPackage());
    factoryField.setText(parameters.getRunClass());
    packageRadioButton.setSelected(parameters.isPackageInUse());
    factoryRadioButton.setSelected(parameters.isFactoryInUse());
    classesRadioButton.setSelected(parameters.isClassesInUse());
  }

  @Override
  protected void applyEditorTo(OSMORunConfig s) throws ConfigurationException {
    OSMORunParameters parameters = s.getRunParameters();
    commonJavaParametersPanel.applyTo(parameters);
    parameters.setAlgorithm(algorithmField.getText());
    parameters.setTestEndCondition(testEndConditionTextField.getText());
    parameters.setSuiteEndCondition(suiteEndConditionTextField.getText());
    parameters.setSeed(seedTextField.getText());
    parameters.setStopOnError(stopOnErrorCheckBox.isSelected());
    parameters.setUnWrapExceptions(unwrapExceptionsCheckBox.isSelected());
    parameters.setPackageInUse(packageRadioButton.isSelected());
    parameters.setFactoryInUse(factoryRadioButton.isSelected());
    parameters.setClassesInUse(classesRadioButton.isSelected());
    parameters.setRunClass(factoryField.getText());
    parameters.setPackageName(packageField.getText());
//    parameters.setListeners(listenersList.getModel().getSize())
//    parameters.setClasses(classesList.getModel())
    
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return topPanel;
  }

  @Override
  protected void disposeEditor() {
  }

  public OSMORunParameters getRunParameters() {
    return parameters;
  }

  public JTextField getPackageField() {
    return packageField;
  }

  public ConfigurationModuleSelector getModuleSelector() {
    return moduleSelector;
  }
}
