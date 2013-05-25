package osmo.tester.ide.intellij;

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
  private JButton button;
  private JTabbedPane tabbedPane;
  private boolean enabled;

  public ModelObjectListener(OSMORunConfigEditor editor, String text, boolean enabled) {
    this.label = editor.getModelObjectLabel();
    this.label2 = editor.getModelObjectLabel2();
    this.button = editor.getModelObjectButton();
    this.tabbedPane = editor.getTabbedPane();
    this.text = text;
    this.enabled = enabled;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    JRadioButton source = (JRadioButton) e.getSource();
    if (source.isSelected()) {
      label.setText(text);
    }
    button.setVisible(enabled);
    label2.setVisible(!enabled);
    tabbedPane.setEnabledAt(1, !enabled);
  }
}
