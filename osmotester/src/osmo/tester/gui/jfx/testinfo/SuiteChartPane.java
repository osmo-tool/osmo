package osmo.tester.gui.jfx.testinfo;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import osmo.tester.coverage.ScoreCalculator;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.testinfo.TestInfoWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class SuiteChartPane extends VBox {
  private final GUIState state;
  private final CheckBox showOverall = new CheckBox("Score");
  private final CheckBox showSteps = new CheckBox("Steps");
  private final CheckBox showStepPairs = new CheckBox("Step Pairs");
  private final CheckBox showStates = new CheckBox("States");
  private final CheckBox showStatePairs = new CheckBox("State Pairs");
  private final CheckBox showVariables = new CheckBox("Variables");
  private final CheckBox showValues = new CheckBox("Values");
  private final CheckBox showRequirements = new CheckBox("Requirements");
  private final LineChart<Number,Number> chart;
  private List<TestCase> shown = null;

  public SuiteChartPane(GUIState state) {
    super(10);
    this.state = state;

    showOverall.setOnAction((event) -> refresh());
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
    chart.setTitle("Score evolution over tests in iteration");

    chart.setCreateSymbols(false);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(showOverall, 0, 0);
    grid.add(showSteps,0, 1);
    grid.add(showStepPairs, 1, 0);
    grid.add(showStates, 1, 1);
    grid.add(showStatePairs, 2, 0);
    grid.add(showValues, 2, 1);
    grid.add(showVariables, 3, 0);
    grid.add(showRequirements, 3, 1);

    ObservableList<Node> kids = getChildren();
    kids.add(chart);
    kids.add(grid);
    Button showButton = new Button("Show Tests");
    showButton.setOnAction((event) -> showTests());
    kids.add(showButton);
    
    setChartSettings(state.getChartSettings());
  }
  
  public void visualize(List<TestCase> tests) {
    this.shown = tests;
    XYChart.Series<Number, Number> overallSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> stepsSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> stepPairsSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> statesSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> statePairsSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> variablesSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> valuesSeries = new XYChart.Series<>();
    XYChart.Series<Number, Number> requirementsSeries = new XYChart.Series<>();

    overallSeries.setName("Overall Score");
    stepsSeries.setName("Steps");
    stepPairsSeries.setName("Step Pairs");
    statesSeries.setName("States");
    statePairsSeries.setName("State Pairs");
    variablesSeries.setName("Variables");
    valuesSeries.setName("Values");
    requirementsSeries.setName("Requirements");

    ObservableList<XYChart.Data<Number, Number>> overallSeriesData = overallSeries.getData();
    ObservableList<XYChart.Data<Number, Number>> stepsSeriesData = stepsSeries.getData();
    ObservableList<XYChart.Data<Number, Number>> stepPairsSeriesData = stepPairsSeries.getData();
    ObservableList<XYChart.Data<Number, Number>> statesSeriesData = statesSeries.getData();
    ObservableList<XYChart.Data<Number, Number>> statePairsSeriesData = statePairsSeries.getData();
    ObservableList<XYChart.Data<Number, Number>> variablesSeriesData = variablesSeries.getData();
    ObservableList<XYChart.Data<Number, Number>> valuesSeriesData = valuesSeries.getData();
    ObservableList<XYChart.Data<Number, Number>> requirementsSeriesData = requirementsSeries.getData();
    
    TestCoverage tc = new TestCoverage();
    int i = 1;
    ScoreCalculator sc = new ScoreCalculator(state.getScoreConfig());
    for (TestCase test : tests) {
      tc.addCoverage(test.getCoverage());
      overallSeriesData.add(new XYChart.Data<>(i, sc.calculateScore(tc)));
      stepsSeriesData.add(new XYChart.Data<>(i, tc.getSingles().size()));
      stepPairsSeriesData.add(new XYChart.Data<>(i, tc.getStepPairs().size()));
      statesSeriesData.add(new XYChart.Data<>(i, tc.getStateCount()));
      statePairsSeriesData.add(new XYChart.Data<>(i, tc.getStatePairCount()));
      variablesSeriesData.add(new XYChart.Data<>(i, tc.getVariables().size()));
      valuesSeriesData.add(new XYChart.Data<>(i, tc.getValueCount()));
      requirementsSeriesData.add(new XYChart.Data<>(i, tc.getRequirements().size()));
      i++;
    }

    List<XYChart.Series<Number, Number>> lines = new ArrayList<>();
    if (showOverall.isSelected()) lines.add(overallSeries);
    if (showSteps.isSelected()) lines.add(stepsSeries);
    if (showStepPairs.isSelected()) lines.add(stepPairsSeries);
    if (showStates.isSelected()) lines.add(statesSeries);
    if (showStatePairs.isSelected()) lines.add(statePairsSeries);
    if (showVariables.isSelected()) lines.add(variablesSeries);
    if (showValues.isSelected()) lines.add(valuesSeries);
    if (showRequirements.isSelected()) lines.add(requirementsSeries);
    chart.getData().setAll(lines);
  }
  
  public void refresh() {
    visualize(shown);
  }
  
  private void showTests() {
    TestInfoWindow tiw = new TestInfoWindow(state);
    tiw.addTests(shown);
    tiw.show();
  }
  
  public void setChartSettings(ChartSettings settings) {
    showOverall.setSelected(settings.showOverall);
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
    settings.showOverall = showOverall.isSelected();
    settings.showSteps = showSteps.isSelected();
    settings.showStepPairs = showStepPairs.isSelected();
    settings.showStates = showStates.isSelected();
    settings.showStatePairs = showStatePairs.isSelected();
    settings.showValues = showValues.isSelected();
    settings.showVariables = showVariables.isSelected();
    settings.showRequirements = showRequirements.isSelected();
  }
}
