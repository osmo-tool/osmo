package osmo.tester.gui.jfx.executiontab.single;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import osmo.tester.coverage.TestCoverage;

/**
 * @author Teemu Kanstren
 */
public class MetricsPane extends GridPane {
  private TestCoverage coverage;
  private int testCount = 0;
  private int iterationCount = 0;
  private final TextField iterationField = new TextField();
  private final TextField testsField = new TextField();
  private final TextField lengthField = new TextField();
  private final TextField stepsField = new TextField();
  private final TextField stepPairsField = new TextField();
  private final TextField stateField = new TextField();
  private final TextField statePairsField = new TextField();
  private final TextField valuesField = new TextField();
  private final TextField requirementsField = new TextField();
  
  public MetricsPane(boolean showTests, boolean showIterations) {
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(10, 10, 10, 10));
    Label iterationLabel = new Label("Iterations:");
    Label testsLabel = new Label("Tests:");
    Label lengthLabel = new Label("Length:");
    Label stepsLabel = new Label("Steps:");
    Label stepPairsLabel = new Label("Step-Pairs:");
    Label stateLabel = new Label("States:");
    Label statePairsLabel = new Label("State-Pairs:");
    Label valuesLabel = new Label("Values:");
    Label requirementsLabel = new Label("Requirements:");

    iterationField.setEditable(false);
    testsField.setEditable(false);
    lengthField.setEditable(false);
    stepsField.setEditable(false);
    stepPairsField.setEditable(false);
    stateField.setEditable(false);
    statePairsField.setEditable(false);
    valuesField.setEditable(false);
    requirementsField.setEditable(false);

    iterationField.setPrefColumnCount(10);
    testsField.setPrefColumnCount(10);
    lengthField.setPrefColumnCount(10);
    stepsField.setPrefColumnCount(10);
    stepPairsField.setPrefColumnCount(10);
    stateField.setPrefColumnCount(10);
    statePairsField.setPrefColumnCount(10);
    valuesField.setPrefColumnCount(10);
    requirementsField.setPrefColumnCount(10);

    int y=0;
    if (showIterations) add(iterationLabel, 0, y);
    if (showIterations) add(iterationField, 1, y++);
    if (showTests) add(testsLabel, 0, y);
    if (showTests) add(testsField, 1, y++);
    add(lengthLabel, 0, y);
    add(lengthField, 1, y++);
    add(stepsLabel, 0, y);
    add(stepsField, 1, y++);
    add(stepPairsLabel, 0, y);
    add(stepPairsField, 1, y++);
    add(stateLabel, 0, y);
    add(stateField, 1, y++);
    add(statePairsLabel, 0, y);
    add(statePairsField, 1, y++);
    add(valuesLabel, 0, y);
    add(valuesField, 1, y++);
    add(requirementsLabel, 0, y);
    add(requirementsField, 1, y++);
  }

  public void setCoverage(TestCoverage coverage) {
    this.coverage = coverage;
  }
  
  public void increaseTestCount() {
    testCount++;
  }
  
  public void increaseIterationCount() {
    iterationCount++;
  }

  public void refresh() {
    iterationField.setText("" + iterationCount);
    testsField.setText("" + testCount);
    lengthField.setText("" + coverage.getTotalSteps());
    stepsField.setText("" + coverage.getSingles().size());
    stepPairsField.setText("" + coverage.getStepPairs().size());
    stateField.setText(""+coverage.getStateCount());
    statePairsField.setText(""+coverage.getStatePairCount());
    valuesField.setText(""+coverage.getValueCount());
    requirementsField.setText(""+coverage.getRequirements().size());
  }

  public void setTestCount(int testCount) {
    this.testCount = testCount;
  }
}
