package osmo.tester.gui.manualdrive;

import osmo.tester.model.data.DataType;
import osmo.tester.model.data.SearchableInput;
import osmo.tester.model.data.ValueRangeSet;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.HeadlessException;

/**
 * GUI to manually define value for a value range set.
 *
 * @author Teemu Kanstren
 */
public class ValueRangeSetGUI extends ValueGUI {
  /** For the user to define the value. */
  private JTextArea area;

  public ValueRangeSetGUI(SearchableInput input) throws HeadlessException {
    super(input);
    pack();
  }

  @Override
  protected String createValueLabel() {
    return "Give value (number)";
  }

  @Override
  protected JComponent createValueComponent() {
    area = new JTextArea();
    return area;
  }

  @Override
  protected Object value() {
    ValueRangeSet range = (ValueRangeSet) input;
    DataType type = range.getPartition(0).getType();
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
    } catch (NumberFormatException e) {
      area.setBackground(Color.RED);
      return null;
    }
    return value;
  }

  public static void main(String[] args) {
    ValueRangeSetGUI gui = new ValueRangeSetGUI(new ValueRangeSet());
    gui.setVisible(true);
  }
}
