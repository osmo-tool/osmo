package osmo.tester.gui.manualdrive;

import osmo.tester.gui.ModelHelper;
import osmo.tester.model.dataflow.SearchableInput;
import osmo.tester.model.dataflow.ValueSet;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class ValueSetGUI extends ValueGUI {
  private JComboBox combo;

  public ValueSetGUI(SearchableInput input) throws HeadlessException {
    super(input);
  }

  @Override
  protected void build() {
  }

  @Override
  protected String createValueLabel() {
    return "Pick option from list";
  }

  @Override
  protected JComponent createValueComponent() {
    ValueSet set = (ValueSet) input;
    Collection options = set.getOptions();
    List choices = new ArrayList(options);
    combo = new JComboBox(choices.toArray());
    return combo;
  }

  @Override
  protected Object value() {
    return combo.getSelectedItem();
  }

  public static void main(String[] args) {
    ValueSetGUI gui = new ValueSetGUI(new ValueSet<String>("one", "two"));
    gui.setVisible(true);
  }
}
