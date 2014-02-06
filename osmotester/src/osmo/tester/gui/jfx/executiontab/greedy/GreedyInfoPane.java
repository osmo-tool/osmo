package osmo.tester.gui.jfx.executiontab.greedy;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.executiontab.basic.MetricsPane;
import osmo.tester.gui.jfx.executiontab.basic.ReducerPane;
import osmo.tester.gui.jfx.executiontab.basic.TestInfoPane;

import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class GreedyInfoPane extends GridPane {
  private final MetricsPane metricsPane = new MetricsPane(true, true);
  private final IterationInfoPane iterationInfoPane;
  private final TextField iterationField = new TextField();

  public GreedyInfoPane(GUIState state) {
    iterationInfoPane = new IterationInfoPane(state);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(10, 10, 10, 10));
    VBox vbox = createLeftPane();
    add(vbox, 0, 0);
    add(iterationInfoPane, 1, 0);
  }

  private VBox createLeftPane() {
    Label iterationLabel = new Label("Iteration: ");
    iterationField.setEditable(false);
    iterationField.setPrefColumnCount(5);

    HBox hbox = new HBox(10);
    Button stop = new Button("Stop");
    hbox.setAlignment(Pos.CENTER);
    hbox.getChildren().add(stop);

    VBox vbox = new VBox(0);
    ObservableList<Node> kids = vbox.getChildren();
    kids.add(new Label("Generation Metrics"));
    kids.add(metricsPane);
    kids.add(hbox);
    return vbox;
  }

  public void addIteration(List<TestCase> tests) {
    
  }
}
