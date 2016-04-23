package osmo.tester.gui.manualdrive;

import osmo.tester.model.data.SearchableInput;
import osmo.tester.model.data.Text;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.HeadlessException;

/**
 * GUI to manually define value for readable text.
 *
 * @author Teemu Kanstren
 */
public class TextGUI extends ValueGUI {
  /** For the user to define the value. */
  private JTextArea area;

  public TextGUI(SearchableInput input) throws HeadlessException {
    super(input);
    pack();
  }

  @Override
  protected String createValueLabel() {
    return "Give value (text)";
  }

  @Override
  protected JComponent createValueComponent() {
    area = new JTextArea();
    return area;
  }

  @Override
  protected Object value() {
    String text = area.getText();
    if (text.length() == 0) {
      area.setBackground(Color.RED);
      return null;
    }
    return text;
  }

  public static void main(String[] args) {
    Text text = new Text();
    text.setName("userName");
    TextGUI gui = new TextGUI(text);
    gui.setVisible(true);
  }
}
