package osmo.tester.gui.manualdrive;

import osmo.tester.model.dataflow.Words;
import osmo.tester.model.dataflow.SearchableInput;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.HeadlessException;

/**
 * GUI to manually define value for a readable word.
 *
 * @author Teemu Kanstren
 */
public class WordGUI extends ValueGUI {
  /** For the user to define the value. */
  private JTextArea area;

  public WordGUI(SearchableInput input) throws HeadlessException {
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
    Words words = new Words();
    words.setName("userName");
    WordGUI gui = new WordGUI(words);
    gui.setVisible(true);
  }
}
