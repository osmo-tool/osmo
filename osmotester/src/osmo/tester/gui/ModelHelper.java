package osmo.tester.gui;

import osmo.tester.model.FSMTransition;

import javax.swing.AbstractListModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Helps to add items to the list
 *
 * @author Olli-Pekka Puolitaival
 */
public class ModelHelper extends AbstractListModel {
  private static final long serialVersionUID = 1L;
  private List<String> values = new ArrayList<String>();

  public ModelHelper() {
    this.values.add("Empty");
  }

  public ModelHelper(List<FSMTransition> transitions) {
    values = new ArrayList<String>();
    for (FSMTransition t : transitions) {
      values.add(t.getName());
    }
  }

  public int getSize() {
    return values.size();
  }

  public Object getElementAt(int index) {
    return values.get(index);
  }
}
