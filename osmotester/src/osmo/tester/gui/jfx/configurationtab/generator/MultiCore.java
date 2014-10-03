package osmo.tester.gui.jfx.configurationtab.generator;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.configurationtab.endconditions.ECDescription;
import osmo.tester.gui.jfx.configurationtab.endconditions.EndlessDescription;
import osmo.tester.gui.jfx.configurationtab.endconditions.LengthDescription;
import osmo.tester.gui.jfx.configurationtab.endconditions.LengthProbabilityDescription;
import osmo.tester.gui.jfx.configurationtab.endconditions.ProbabilityDescription;
import osmo.tester.gui.jfx.configurationtab.endconditions.TimeDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class MultiCore extends SingleCore {
  private static final int parallelismY = 5;

  public MultiCore(GUIState state) {
    super(state);
  }

  @Override
  public String toString() {
    return "Multi Core";
  }
  
  @Override
  public Pane createPane() {
    createTestECPane();
    createSuiteECPane();
    createAlgorithmPane();
    createParallelismPane();
    return grid;
  }

  private void createParallelismPane() {
    Label label = new Label("Parallelism:");
    HBox hbox = new HBox(10);
    hbox.setAlignment(Pos.CENTER_LEFT);
    TextField field = new TextField();
    hbox.getChildren().add(field);
    hbox.getChildren().add(new CheckBox("System"));
    field.setPrefColumnCount(2);
    grid.add(label, 0, parallelismY);
    grid.add(hbox, 1, parallelismY);
  }
}
