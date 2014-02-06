package osmo.tester.gui.jfx.configurationtab.endconditions;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.gui.jfx.configurationtab.GeneratorPane;
import osmo.tester.gui.jfx.configurationtab.generator.SingleCore;

/**
 * @author Teemu Kanstren
 */
public class LengthDescription implements ECDescription {
  private Length length = new Length(10);

  public void setLength(Length length) {
    this.length = length;
  }

  @Override
  public Length getEndCondition() {
    return length;
  }

  @Override
  public Pane createEditor(SingleCore parent, Stage stage) {
    return new LengthEditor(parent, stage, this);
  }

  @Override
  public boolean supportsEditing() {
    return true;
  }

  @Override
  public String toString() {
    return "Length("+length.getLength()+")";
  }
}
