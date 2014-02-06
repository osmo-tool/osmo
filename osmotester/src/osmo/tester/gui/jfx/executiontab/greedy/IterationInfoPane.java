package osmo.tester.gui.jfx.executiontab.greedy;

import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.gui.jfx.GUIState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class IterationInfoPane extends VBox {
  private final GUIState state;
  private final CheckBox showOverall = new CheckBox();
  private final CheckBox showSteps = new CheckBox();
  private final CheckBox showStepPairs = new CheckBox();
  private final CheckBox showStates = new CheckBox();
  private final CheckBox showStatePairs = new CheckBox();
  private final CheckBox showVariables = new CheckBox();
  private final CheckBox showValues = new CheckBox();
  private final CheckBox showRequirements = new CheckBox();

  private XYChart.Series<Number, Number> overallSeries = new XYChart.Series<>();
  private XYChart.Series<Number, Number> stepsSeries = new XYChart.Series<>();
  private XYChart.Series<Number, Number> stepPairsSeries = new XYChart.Series<>();
  private XYChart.Series<Number, Number> statesSeries = new XYChart.Series<>();
  private XYChart.Series<Number, Number> statePairsSeries = new XYChart.Series<>();
  private XYChart.Series<Number, Number> variablesSeries = new XYChart.Series<>();
  private XYChart.Series<Number, Number> valuesSeries = new XYChart.Series<>();
  private XYChart.Series<Number, Number> requirementsSeries = new XYChart.Series<>();
  private final LineChart<Number,Number> chart;

  public IterationInfoPane(GUIState state) {
    super(10);
    this.state = state;

    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Tests");
    yAxis.setLabel("Score");
    chart = new LineChart<>(xAxis, yAxis);
    chart.setTitle("Score evolution over tests in set");

    overallSeries.setName("Overall Score");
    stepsSeries.setName("Steps");
    stepPairsSeries.setName("Step Pairs");
    statesSeries.setName("States");
    statePairsSeries.setName("State Pairs");
    variablesSeries.setName("Variables");
    valuesSeries.setName("Values");
    requirementsSeries.setName("Requirements");

    chart.setCreateSymbols(false);

    getChildren().add(chart);
  }
  
  public void visualize(List<TestCase> tests) {
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
    for (TestCase test : tests) {
      tc.addCoverage(test.getCoverage());
      stepsSeriesData.add(new XYChart.Data<>(i, tc.getSingles().size()));
      stepPairsSeriesData.add(new XYChart.Data<>(i, tc.getStepPairs().size()));
      statesSeriesData.add(new XYChart.Data<>(i, tc.getStateCount()));
      statePairsSeriesData.add(new XYChart.Data<>(i, tc.getStatePairCount()));
      variablesSeriesData.add(new XYChart.Data<>(i, tc.getVariables().size()));
      valuesSeriesData.add(new XYChart.Data<>(i, tc.getValueCount()));
      requirementsSeriesData.add(new XYChart.Data<>(i, tc.getRequirements().size()));
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
}
