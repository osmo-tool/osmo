package osmo.tester.gui.jfx.configurationtab;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author Teemu Kanstren
 */
public class AdvancedPane extends VBox {
  public AdvancedPane() {
    super(10);
    CheckBox cb1 = new CheckBox("Fail when no way forward");
    CheckBox cb2 = new CheckBox("Stop test on error");
    CheckBox cb3 = new CheckBox("Stop generation on error");
    CheckBox cb4 = new CheckBox("Unwrap exceptions");
    CheckBox cb5 = new CheckBox("Write sequence trace");
    CheckBox cb6 = new CheckBox("Collect data trace");
    CheckBox cb7 = new CheckBox("Print exploration errors");
    ObservableList<Node> children = getChildren();
    children.add(new Label("Advanced Settings"));
    children.add(cb1);
    children.add(cb2);
    children.add(cb3);
    children.add(cb4);
    children.add(cb5);
    children.add(cb6);
    children.add(cb7);
  }

}
