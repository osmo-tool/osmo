package osmo.tester.gui.jfx.testinfo;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.optimizer.reducer.Reducer;
import osmo.tester.optimizer.reducer.ReducerConfig;
import osmo.tester.optimizer.reducer.ReducerState;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Teemu Kanstren
 */
public class ReducerPane extends GridPane {
  private final GUIState state;
  private final TextField populationField = new TextField();
  private final TextField timeoutField = new TextField();
  private final ComboBox<TimeUnit> timeoutBox = new ComboBox<>();
  private final TextField iterationTimeoutField = new TextField();
  private final ComboBox<TimeUnit> iterationUnitBox = new ComboBox<>();
  private final TestInfoPane tiPane;
  private final TextField parallelField = new TextField();

  public ReducerPane(GUIState state, TestInfoPane tiPane) {
    this.state = state;
    this.tiPane = tiPane;
    setHgap(10);
    setVgap(10);
    add(new Label("Reducer"), 0, 0);

    createTimeoutCombo(1);
    createIterationCombo(2);
    createPopulationField(3);
    createParallelField();

    Button button = new Button("Reduce");
    button.setOnAction((event) -> runReducer());
    add(button, 0, 5, 3, 1);
    
    setParameters();
  }

  private void createParallelField() {
    Label parallelLabel = new Label("Parallelism:");
    parallelField.setPrefColumnCount(3);
    add(parallelLabel, 0, 4);
    add(parallelField, 1, 4);
  }

  private void createPopulationField(int y) {
    Label populationLabel = new Label("Population:");
    populationField.setPrefColumnCount(3);
    add(populationLabel, 0, y);
    add(populationField, 1, y);
  }

  private void createTimeoutCombo(int y) {
    Label labelDelay = new Label("Timeout:");
    timeoutField.setPrefColumnCount(3);
    add(labelDelay, 0, y);
    add(timeoutField, 1, y);

    ObservableList<TimeUnit> items = timeoutBox.getItems();
    items.addAll(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS);
    timeoutBox.setValue(TimeUnit.MINUTES);
    timeoutField.setText("x");
    add(timeoutBox, 2, y);
  }

  private void createIterationCombo(int y) {
    Label labelDelay = new Label("Iteration:");
    iterationTimeoutField.setPrefColumnCount(3);
    add(labelDelay, 0, y);
    add(iterationTimeoutField, 1, y);

    ObservableList<TimeUnit> items = iterationUnitBox.getItems();
    items.addAll(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS);
    iterationUnitBox.setValue(TimeUnit.MINUTES);
    add(iterationUnitBox, 2, y);
  }
  
  private void runReducer() {
    ReducerConfig config = new ReducerConfig(state.getSeed());
    Reducer reducer = new Reducer(config);
    OSMOConfiguration osmoConfig = reducer.getOsmoConfig();
    osmoConfig.setFactory(state.getOsmoConfig().getFactory());
    ReducerState result = reducer.search();
    List<TestCase> tests = result.getTests();
    for (TestCase test : tests) {
      tiPane.addTest(test);
    }
  }
  
  private void setParameters() {
    ReducerConfig config = state.getReducerConfig();
    populationField.setText(""+config.getPopulationSize());
    timeoutField.setText(""+config.getTotalTime());
    timeoutBox.setValue(config.getTotalUnit());
    iterationTimeoutField.setText(""+config.getIterationTime());
    iterationUnitBox.setValue(config.getIterationUnit());
    parallelField.setText(""+config.getParallelism());
  }
}
