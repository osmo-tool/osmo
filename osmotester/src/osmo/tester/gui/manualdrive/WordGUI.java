package osmo.tester.gui.manualdrive;

import osmo.tester.model.dataflow.ReadableWords;
import osmo.tester.model.dataflow.SearchableInput;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import java.awt.*;

/**
 * @author Teemu Kanstren
 */
public class WordGUI extends ValueGUI {
  private JTextArea area;

  public WordGUI(SearchableInput input) throws HeadlessException {
    super(input);
    pack();
  }

  @Override
  protected void build() {
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
    WordGUI gui = new WordGUI(new ReadableWords());
    gui.setVisible(true);
  }
}
