package osmo.tester.gui.jfx.executiontab.greedy;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.executiontab.single.MetricsPane;
import osmo.tester.gui.jfx.testinfo.SuiteChartPane;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class GreedyInfoPane extends GridPane {
  private final GUIState state;
  private final MetricsPane metricsPane = new MetricsPane(true, true);
  private final SuiteChartPane iterationInfoPane;
  private final ListView<Iteration> iterations = new ListView<>();

  public GreedyInfoPane(GUIState state) {
    this.state = state;
    iterationInfoPane = new SuiteChartPane(state);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(10, 10, 10, 10));
    VBox vbox = createLeftPane();
    add(vbox, 0, 0);
    add(iterationInfoPane, 1, 0);
    ReadOnlyObjectProperty<Iteration> selectedProperty = iterations.getSelectionModel().selectedItemProperty();
    selectedProperty.addListener((observable, oldValue, newValue) -> choice(newValue));
  }

  private void choice(Iteration iteration) {
    iterationInfoPane.visualize(iteration, true);
  }

  private VBox createLeftPane() {
    HBox hbox = new HBox(10);
    hbox.setPadding(new Insets(0, 0, 10, 0));
    Button stop = new Button("Stop");
    hbox.setAlignment(Pos.CENTER);
    hbox.getChildren().add(stop);

    VBox vbox = new VBox(0);
    ObservableList<Node> kids = vbox.getChildren();
    kids.add(new Label("Generation Metrics"));
    kids.add(metricsPane);
    kids.add(hbox);
    kids.add(iterations);
    return vbox;
  }

  public void addIteration(List<TestCase> iterationTests) {
    List<TestCase> tests = new ArrayList<>(iterationTests);
    Platform.runLater(() -> {
      iterations.getItems().add(new Iteration(tests));
      metricsPane.increaseIterationCount();
      metricsPane.setCoverage(new TestCoverage(tests));
      metricsPane.refresh();
      metricsPane.setTestCount(tests.size());
    });
  }
}
