package osmo.tester.gui.jfx.configurationtab.endconditions;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.gui.jfx.configurationtab.BasicsPane;

/**
 * @author Teemu Kanstren
 */
public interface ECDescription {
  public EndCondition getEndCondition();
  public Pane createEditor(BasicsPane parent, Stage stage);
  public boolean supportsEditing();
}
