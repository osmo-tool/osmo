package osmo.tester.gui.jfx.configurationtab;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import osmo.common.Randomizer;
import osmo.tester.OSMOConfiguration;
import osmo.tester.gui.jfx.GUIState;

/**
 * @author Teemu Kanstren
 */
public class GeneralPane extends VBox {
  private final GUIState state;
  private final TextField seedField = new TextField();
  private final Randomizer rand = new Randomizer();

  public GeneralPane(GUIState state) {
    super(10);
    this.state = state;
    CheckBox cb1 = new CheckBox("Fail when no way forward");
    CheckBox cb2 = new CheckBox("Stop test on error");
    CheckBox cb3 = new CheckBox("Stop generation on error");
    CheckBox cb4 = new CheckBox("Unwrap exceptions");
    CheckBox cb5 = new CheckBox("Write sequence trace");
    CheckBox cb6 = new CheckBox("Collect data trace");
    CheckBox cb7 = new CheckBox("Print exploration errors");

    OSMOConfiguration osmoConfig = state.getOsmoConfig();
    cb1.setSelected(osmoConfig.shouldFailWhenNoWayForward());
    cb2.setSelected(osmoConfig.shouldStopTestOnError());
    cb3.setSelected(osmoConfig.shouldStopGenerationOnError());
    cb4.setSelected(osmoConfig.shouldUnwrapExceptions());
    cb5.setSelected(osmoConfig.isSequenceTraceRequested());
    cb6.setSelected(osmoConfig.isDataTraceRequested());
    cb7.setSelected(osmoConfig.isPrintExplorationErrors());

    Pane seedPane = createSeedPane();
    state.setSeedField(seedField);

    ObservableList<Node> children = getChildren();
    children.add(new Label("General Settings"));
    children.add(cb1);
    children.add(cb2);
    children.add(cb3);
    children.add(cb4);
    children.add(cb5);
    children.add(cb6);
    children.add(cb7);
    children.add(seedPane);
  }

  public Pane createSeedPane() {
    HBox hbox = new HBox(10);
    hbox.setAlignment(Pos.CENTER_LEFT);
    Label label = new Label("Seed:");
    seedField.setPrefColumnCount(15);
    Button button = new Button("Randomize");
    hbox.getChildren().add(label);
    hbox.getChildren().add(seedField);
    hbox.getChildren().add(button);
    button.setOnAction((event) -> seedField.setText(""+rand.nextLong()));
    return hbox;
  }
}
