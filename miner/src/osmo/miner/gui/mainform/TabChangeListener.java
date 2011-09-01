package osmo.miner.gui.mainform;

import osmo.miner.gui.mainform.programmodel.AllForm;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class TabChangeListener implements ChangeListener {
  private final List<ModelObject> objects;
  private final AllForm allForm;

  public TabChangeListener(List<ModelObject> objects, AllForm allForm) {
    this.objects = objects;
    this.allForm = allForm;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    JTabbedPane pane = (JTabbedPane) e.getSource();
    int index = pane.getSelectedIndex();
    if (index == 2) {
      System.out.println("change");
    }
  }
}
