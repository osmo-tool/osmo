package osmo.tester.gui.jfx.testinfo;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.executiontab.greedy.Iteration;
import osmo.tester.scripter.internal.TestWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class SuiteChartPane extends VBox {
  private final GUIState state;
  private final CheckBox showScore = new CheckBox("Score");
  private final CheckBox showSteps = new CheckBox("Steps");
  private final CheckBox showStepPairs = new CheckBox("Step Pairs");
  private final CheckBox showStates = new CheckBox("States");
  private final CheckBox showStatePairs = new CheckBox("State Pairs");
  private final CheckBox showVariables = new CheckBox("Variables");
  private final CheckBox showValues = new CheckBox("Values");
  private final CheckBox showRequirements = new CheckBox("Requirements");

  private final XYChart.Series<Number, Number> scoreSeries = new XYChart.Series<>();
  private final XYChart.Series<Number, Number> stepsSeries = new XYChart.Series<>();
  private final XYChart.Series<Number, Number> stepPairsSeries = new XYChart.Series<>();
  private final XYChart.Series<Number, Number> statesSeries = new XYChart.Series<>();
  private final XYChart.Series<Number, Number> statePairsSeries = new XYChart.Series<>();
  private final XYChart.Series<Number, Number> variablesSeries = new XYChart.Series<>();
  private final XYChart.Series<Number, Number> valuesSeries = new XYChart.Series<>();
  private final XYChart.Series<Number, Number> requirementsSeries = new XYChart.Series<>();

  private final ObservableList<XYChart.Data<Number, Number>> scoreSeriesData = scoreSeries.getData();
  private final ObservableList<XYChart.Data<Number, Number>> stepsSeriesData = stepsSeries.getData();
  private final ObservableList<XYChart.Data<Number, Number>> stepPairsSeriesData = stepPairsSeries.getData();
  private final ObservableList<XYChart.Data<Number, Number>> statesSeriesData = statesSeries.getData();
  private final ObservableList<XYChart.Data<Number, Number>> statePairsSeriesData = statePairsSeries.getData();
  private final ObservableList<XYChart.Data<Number, Number>> variablesSeriesData = variablesSeries.getData();
  private final ObservableList<XYChart.Data<Number, Number>> valuesSeriesData = valuesSeries.getData();
  private final ObservableList<XYChart.Data<Number, Number>> requirementsSeriesData = requirementsSeries.getData();

  private final LineChart<Number, Number> chart;
  private Iteration shown = null;
  private final ScoreCalculator calculator;
  
  private volatile long previousUpdate = 0;

  public SuiteChartPane(GUIState state) {
    super(10);
    this.state = state;
    this.calculator = new ScoreCalculator(state.getScoreConfig());

    showScore.setOnAction((event) -> refresh());
    showSteps.setOnAction((event) -> refresh());
    showStepPairs.setOnAction((event) -> refresh());
    showStates.setOnAction((event) -> refresh());
    showStatePairs.setOnAction((event) -> refresh());
    showRequirements.setOnAction((event) -> refresh());
    showValues.setOnAction((event) -> refresh());
    showVariables.setOnAction((event) -> refresh());

    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Tests");
    yAxis.setLabel("Score");
    xAxis.setAutoRanging(true);
    yAxis.setAutoRanging(true);
    chart = new LineChart<>(xAxis, yAxis);
    chart.setAnimated(false);
    chart.setTitle("Score evolution over tests in iteration");

    chart.getData().add(scoreSeries);
    chart.getData().add(stepsSeries);
    chart.getData().add(stepPairsSeries);
    chart.getData().add(statesSeries);
    chart.getData().add(statePairsSeries);
    chart.getData().add(variablesSeries);
    chart.getData().add(valuesSeries);
    chart.getData().add(requirementsSeries);


    chart.setCreateSymbols(false);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(showScore, 0, 0);
    grid.add(showSteps, 0, 1);
    grid.add(showStepPairs, 1, 0);
    grid.add(showStates, 1, 1);
    grid.add(showStatePairs, 2, 0);
    grid.add(showValues, 2, 1);
    grid.add(showVariables, 3, 0);
    grid.add(showRequirements, 3, 1);

    ObservableList<Node> kids = getChildren();
    kids.add(chart);
    kids.add(grid);

    HBox buttonBox = new HBox(10);
    kids.add(buttonBox);

    Button showButton = new Button("Show Tests");
    showButton.setOnAction((event) -> showTests());
    buttonBox.getChildren().add(showButton);

    Button saveButton = new Button("Save Tests");
    saveButton.setOnAction((event) -> saveTests());
    buttonBox.getChildren().add(saveButton);

    setChartSettings(state.getChartSettings());

    scoreSeries.setName("Overall Score");
    stepsSeries.setName("Steps");
    stepPairsSeries.setName("Step Pairs");
    statesSeries.setName("States");
    statePairsSeries.setName("State Pairs");
    variablesSeries.setName("Variables");
    valuesSeries.setName("Values");
    requirementsSeries.setName("Requirements");

  }

  public void visualize(Iteration iteration, boolean force) {
    if (!state.isDrawChart()) return;
    //update at most once per second to avoid lag
    long diff = System.currentTimeMillis() - previousUpdate;
    if (!force && diff < 1000) return;

    this.shown = new Iteration(iteration);

    scoreSeriesData.clear();
    stepsSeriesData.clear();
    stepPairsSeriesData.clear();
    statesSeriesData.clear();
    statePairsSeriesData.clear();
    variablesSeriesData.clear();
    valuesSeriesData.clear();
    requirementsSeriesData.clear();

    TestCoverage tc = new TestCoverage();
    List<TestCase> tests = shown.getTests();
    int i = 0;
    //we sample at max 500*1.5 = 600 points for a chart, as too big datasets seem to slow javafx way down
    //1.5 is from rounding errors where 1.49 will still round down to 1 as delta
    int delta = Math.round(tests.size() / 400);
    //delta of zero would be epic fail. or division by zero error. or adding same value forever.
    if (delta < 1) delta = 1;
    for (TestCase test : tests) {
      i++;
      //always have to add the test to coverage to keep overall metrics correct even if we skip the point in chart
      tc.addCoverage(test.getCoverage());
      //always take the first point which is at zero, then pick with delta
      if (i != 0 && i%delta != 0) continue;
      //for some reason it seems we have to recalculate point objects every time or else the chart breaks
      int score = calculator.calculateScore(tc);
      if (showScore.isSelected()) scoreSeriesData.add(new XYChart.Data<>(i, score));
      if (showSteps.isSelected()) stepsSeriesData.add(new XYChart.Data<>(i, tc.getSingles().size()));
      if (showStepPairs.isSelected()) stepPairsSeriesData.add(new XYChart.Data<>(i, tc.getStepPairs().size()));
      if (showStates.isSelected()) statesSeriesData.add(new XYChart.Data<>(i, tc.getStateCount()));
      if (showStatePairs.isSelected()) statePairsSeriesData.add(new XYChart.Data<>(i, tc.getStatePairCount()));
      if (showVariables.isSelected()) variablesSeriesData.add(new XYChart.Data<>(i, tc.getVariables().size()));
      if (showValues.isSelected()) valuesSeriesData.add(new XYChart.Data<>(i, tc.getValueCount()));
      if (showRequirements.isSelected()) requirementsSeriesData.add(new XYChart.Data<>(i, tc.getRequirements().size()));
    }
//    });
  }

  public void addTest(TestCoverage coverage, int i, TestCase test) {
    if (shown == null) shown = new Iteration(new ArrayList<>());
    if (state.isOnlyFailingTests()) {
      if (test.isFailed()) shown.getTests().add(test);
    } else {
      shown.getTests().add(test);
    }
    if (!state.isDrawChart()) return;
    int score = calculator.calculateScore(coverage);
    Platform.runLater(() -> {
      if (showScore.isSelected()) scoreSeriesData.add(new XYChart.Data<>(i, score));
      if (showSteps.isSelected()) stepsSeriesData.add(new XYChart.Data<>(i, coverage.getSingles().size()));
      if (showStepPairs.isSelected()) stepPairsSeriesData.add(new XYChart.Data<>(i, coverage.getStepPairs().size()));
      if (showStates.isSelected()) statesSeriesData.add(new XYChart.Data<>(i, coverage.getStateCount()));
      if (showStatePairs.isSelected()) statePairsSeriesData.add(new XYChart.Data<>(i, coverage.getStatePairCount()));
      if (showVariables.isSelected()) variablesSeriesData.add(new XYChart.Data<>(i, coverage.getVariables().size()));
      if (showValues.isSelected()) valuesSeriesData.add(new XYChart.Data<>(i, coverage.getValueCount()));
      if (showRequirements.isSelected()) requirementsSeriesData.add(new XYChart.Data<>(i, coverage.getRequirements().size()));
    });
  }

  public void refresh() {
    visualize(shown, false);
  }

  public void finalRefresh() {
    visualize(shown, true);
  }

  private void showTests() {
    TestInfoWindow tiw = new TestInfoWindow(state);
    tiw.addTests(shown.getTests());
    tiw.show();
  }

  private void saveTests() {
    DirectoryChooser dirChooser = new DirectoryChooser();
    dirChooser.setTitle("Select Test Directory");
    //this should set it to current working directory
    File currentDir = new File(".");
    dirChooser.setInitialDirectory(currentDir);
    File dir = dirChooser.showDialog(state.getStage());
    //null means user cancelled
    if (dir == null) return;
    TestWriter writer = new TestWriter(dir.getAbsolutePath());
    writer.write(shown.getTests());
  }

  public void setChartSettings(ChartSettings settings) {
    showScore.setSelected(settings.showScore);
    showSteps.setSelected(settings.showSteps);
    showStepPairs.setSelected(settings.showStepPairs);
    showStates.setSelected(settings.showStates);
    showStatePairs.setSelected(settings.showStatePairs);
    showValues.setSelected(settings.showValues);
    showVariables.setSelected(settings.showVariables);
    showRequirements.setSelected(settings.showRequirements);
  }

  public void storeChartSettings() {
    ChartSettings settings = state.getChartSettings();
    settings.showScore = showScore.isSelected();
    settings.showSteps = showSteps.isSelected();
    settings.showStepPairs = showStepPairs.isSelected();
    settings.showStates = showStates.isSelected();
    settings.showStatePairs = showStatePairs.isSelected();
    settings.showValues = showValues.isSelected();
    settings.showVariables = showVariables.isSelected();
    settings.showRequirements = showRequirements.isSelected();
  }

}
