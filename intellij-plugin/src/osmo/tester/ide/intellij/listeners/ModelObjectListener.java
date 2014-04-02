package osmo.tester.ide.intellij.listeners;

import osmo.tester.ide.intellij.OSMORunConfigEditor;
import osmo.tester.ide.intellij.OSMORunParameters;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** @author Teemu Kanstren */
public class ModelObjectListener implements ChangeListener {
  private final JLabel label;
  private final JLabel label2;
  private final String text;
  private final JTextField factoryField;
  private final OSMORunParameters parameters;
  private JButton button;
  private JTabbedPane tabbedPane;
  public static final String PACKAGE = "Package:";
  public static final String FACTORY = "Factory class:";
  public static final String CLASSES = "Classes:";

  public ModelObjectListener(OSMORunConfigEditor editor, String text) {
    this.label = editor.getModelObjectLabel();
    this.label2 = editor.getModelObjectLabel2();
    this.button = editor.getModelObjectButton();
    this.factoryField = editor.getFactoryField();
    this.tabbedPane = editor.getTabbedPane();
    this.parameters = editor.getRunParameters();
    this.text = text;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    JRadioButton source = (JRadioButton) e.getSource();
    if (source.isSelected()) {
      label.setText(text);
      switch (text) {
        case PACKAGE:
          button.setVisible(true);
          label.setVisible(true);
          label2.setVisible(false);
          factoryField.setVisible(false);
          tabbedPane.setEnabledAt(1, false);
          break;
        case FACTORY:
          button.setVisible(true);
          label.setVisible(true);
          label2.setVisible(false);
          factoryField.setVisible(true);
          factoryField.setText(parameters.getFactoryClass());
          tabbedPane.setEnabledAt(1, false);
          break;
        case CLASSES:
          button.setVisible(false);
          label.setVisible(false);
          label2.setVisible(true);
          factoryField.setVisible(false);
          tabbedPane.setEnabledAt(1, true);
          break;
        default:
          throw new IllegalArgumentException("Unknown value:"+text);
      }
    }
  }
}
