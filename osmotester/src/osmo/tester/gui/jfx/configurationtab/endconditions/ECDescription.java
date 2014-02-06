package osmo.tester.gui.jfx.configurationtab.endconditions;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.gui.jfx.configurationtab.GeneratorPane;
import osmo.tester.gui.jfx.configurationtab.generator.SingleCore;

/**
 * @author Teemu Kanstren
 */
public interface ECDescription {
  public EndCondition getEndCondition();
  public Pane createEditor(SingleCore parent, Stage stage);
  public boolean supportsEditing();
}
