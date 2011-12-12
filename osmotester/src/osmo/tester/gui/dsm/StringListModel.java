package osmo.tester.gui.dsm;

import javax.swing.AbstractListModel;
import java.util.List;

/**
 * A model allowing using a JList to display a list of Strings.
 *
 * @author Teemu Kanstren
 */
public class StringListModel extends AbstractListModel {
  private final List<String> items;

  public StringListModel(List<String> items) {
    this.items = items;
  }

  @Override
  public int getSize() {
    return items.size();
  }

  @Override
  public Object getElementAt(int index) {
    return items.get(index);
  }
}
