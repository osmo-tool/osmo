package osmo.tester.gui.jfx.executiontab.basic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.GUIState;

/**
 * @author Teemu Kanstren
 */
public class BasicExecutorPane extends GridPane {
  private final TestCoverage coverage = new TestCoverage();
  private final MetricsPane metricsPane = new MetricsPane(true, false);
  private final TestInfoPane singleInfoPane;

  public BasicExecutorPane(GUIState state) {
    singleInfoPane = new TestInfoPane(state);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(10, 10, 10, 10));
    VBox vbox = createLeftPane();
    add(vbox, 0, 0);
    add(singleInfoPane, 1, 0, 1, 2);
    add(new ReducerPane(), 2, 0, 1, 2);
  }

  private VBox createLeftPane() {
    metricsPane.setCoverage(coverage);
    HBox hbox = new HBox(10);
    Button stop = new Button("Stop");
    hbox.setAlignment(Pos.CENTER);
    hbox.getChildren().add(stop);

    VBox vbox = new VBox(0);
    vbox.getChildren().add(new Label("Generation Metrics"));
    vbox.getChildren().add(metricsPane);
    vbox.getChildren().add(hbox);
    return vbox;
  }

  public void testEnded(TestCase test) {
    coverage.addCoverage(test.getCoverage());
    metricsPane.increaseTestCount();
    metricsPane.refresh();
    singleInfoPane.addTest(test);
  }
}
