package osmo.miner.gui.mainform;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class MOListModel implements ListModel {
  private final List<ModelObject> objects;
  private Collection<ListDataListener> listeners = new ArrayList<ListDataListener>();

  public MOListModel(List<ModelObject> objects) {
    this.objects = objects;
  }

  @Override
  public int getSize() {
    return objects.size();
  }

  @Override
  public Object getElementAt(int index) {
    return objects.get(index);
  }

  public void add(ModelObject mo) {
    if (objects.contains(mo)) {
      return;
    }
    objects.add(mo);
    Collections.sort(objects);
    for (ListDataListener listener : listeners) {
      int index = objects.size();
      listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, index));
    }
  }

  @Override
  public void addListDataListener(ListDataListener l) {
    listeners.add(l);
  }

  @Override
  public void removeListDataListener(ListDataListener l) {
    listeners.remove(l);
  }
}
