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
  private final CheckBox failIfNoStep = new CheckBox("Fail when no steps");
  private final CheckBox stopTestOnError = new CheckBox("Stop test on error");
  private final CheckBox stopGenerationOnError = new CheckBox("Stop generation on error");
  private final CheckBox sequenceTrace = new CheckBox("Write sequence trace");
  private final CheckBox dataTrace = new CheckBox("Collect data trace");
  private final CheckBox printErrors = new CheckBox("Print exploration errors");
  private final CheckBox drawChart = new CheckBox("Draw chart");
  private final CheckBox keepFailedOnly = new CheckBox("Keep only failing tests");

  public GeneralPane(GUIState state) {
    super(10);
    this.state = state;

    OSMOConfiguration osmoConfig = state.getOsmoConfig();
    failIfNoStep.setSelected(osmoConfig.shouldFailWhenNoWayForward());
    stopTestOnError.setSelected(osmoConfig.shouldStopTestOnError());
    stopGenerationOnError.setSelected(osmoConfig.shouldStopGenerationOnError());
    sequenceTrace.setSelected(osmoConfig.isSequenceTraceRequested());
    dataTrace.setSelected(osmoConfig.isDataTraceRequested());
    printErrors.setSelected(osmoConfig.isPrintExplorationErrors());
    drawChart.setSelected(state.isDrawChart());
    keepFailedOnly.setSelected(state.isOnlyFailingTests());

    Pane seedPane = createSeedPane();
    state.setSeedField(seedField);

    ObservableList<Node> children = getChildren();
    children.add(new Label("General Settings"));
    children.add(failIfNoStep);
    children.add(stopTestOnError);
    children.add(stopGenerationOnError);
    children.add(sequenceTrace);
    children.add(dataTrace);
    children.add(printErrors);
    children.add(drawChart);
    children.add(keepFailedOnly);
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


  public void storeParameters() {
    OSMOConfiguration config = state.getOsmoConfig();
    config.setFailWhenNoWayForward(failIfNoStep.isSelected());
    config.setStopTestOnError(stopTestOnError.isSelected());
    config.setStopGenerationOnError(stopGenerationOnError.isSelected());
    config.setSequenceTraceRequested(sequenceTrace.isSelected());
    config.setDataTraceRequested(dataTrace.isSelected());
    config.setPrintExplorationErrors(printErrors.isSelected());
    config.setKeepTests(keepFailedOnly.isSelected());
    state.setDrawChart(drawChart.isSelected());
    state.setOnlyFailingTests(keepFailedOnly.isSelected());
  }
}
