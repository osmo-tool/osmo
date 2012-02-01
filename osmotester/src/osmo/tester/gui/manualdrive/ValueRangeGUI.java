package osmo.tester.gui.manualdrive;

import osmo.tester.model.dataflow.DataType;
import osmo.tester.model.dataflow.SearchableInput;
import osmo.tester.model.dataflow.ValueRange;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.HeadlessException;

/**
 * GUI to manually define value for a value range.
 *
 * @author Teemu Kanstren
 */
public class ValueRangeGUI extends ValueGUI {
  /** For the user to define the value. */
  private JTextArea area;

  public ValueRangeGUI(SearchableInput input) throws HeadlessException {
    super(input);
    pack();
  }

  @Override
  protected String createValueLabel() {
    ValueRange range = (ValueRange) input;
    return "Give value (range [" + range.min() + " - " + range.max() + "])";
  }

  @Override
  protected JComponent createValueComponent() {
    area = new JTextArea();
    return area;
  }

  public static void main(String[] args) {
    ValueRange range = new ValueRange(1, 100);
    range.enableGUI();

//    ValueRangeGUI gui = new ValueRangeGUI(range);
    System.out.println(range.next());
  }

  @Override
  protected Object value() {
    ValueRange range = (ValueRange) input;
    DataType type = range.getType();
    String parseMe = area.getText();
    try {
      switch (type) {
        case INT:
          value = Integer.parseInt(parseMe);
          break;
        case LONG:
          value = Long.parseLong(parseMe);
          break;
        case DOUBLE:
          value = Double.parseDouble(parseMe);
          break;
        default:
          throw new IllegalArgumentException("Enum type:" + type + " unsupported.");
      }
    } catch (NumberFormatException ne) {
      area.setBackground(Color.RED);
      return null;
    }
    return value;
  }
}
