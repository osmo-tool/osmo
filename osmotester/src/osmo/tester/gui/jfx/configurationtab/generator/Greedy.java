package osmo.tester.gui.jfx.configurationtab.generator;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import osmo.tester.generator.algorithm.FSMTraversalAlgorithm;
import osmo.tester.generator.endcondition.EndCondition;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.gui.jfx.configurationtab.algorithms.AlgorithmDescription;
import osmo.tester.gui.jfx.configurationtab.algorithms.BalancingDescription;
import osmo.tester.gui.jfx.configurationtab.algorithms.RandomDescription;
import osmo.tester.gui.jfx.configurationtab.algorithms.WeightedBalancingDescription;
import osmo.tester.gui.jfx.configurationtab.algorithms.WeightedRandomDescription;
import osmo.tester.gui.jfx.configurationtab.endconditions.ECDescription;
import osmo.tester.gui.jfx.configurationtab.endconditions.EndlessDescription;
import osmo.tester.gui.jfx.configurationtab.endconditions.LengthDescription;
import osmo.tester.gui.jfx.configurationtab.endconditions.LengthProbabilityDescription;
import osmo.tester.gui.jfx.configurationtab.endconditions.ProbabilityDescription;
import osmo.tester.gui.jfx.configurationtab.endconditions.TimeDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Teemu Kanstren
 */
public class Greedy implements GeneratorDescription {
  private final GUIState state;
  protected final GridPane grid = new GridPane();
  private static final int thresholdY = 0;
  private static final int timeoutY = 1;
  private static final int maxY = 2;
  private static final int populationY = 3;
  private static final int testY = 4;
  private static final int algoY = 5;
  private final TextField populationField = new TextField();
  private final TextField maxTestsField = new TextField();
  private final TextField thresholdField = new TextField();
  private final TextField timeoutField = new TextField();
  private final ComboBox<TimeUnit> timeUnitComboBox = new ComboBox<>();
  private ComboBox<ECDescription> oldTestBox = null;
  private Button testButton;
  private ECDescription chosenTestEC = null;
  private List<ECDescription> testECs = new ArrayList<>();
  private ComboBox<AlgorithmDescription> algoBox;

  public Greedy(GUIState state) {
    this.state = state;
    grid.setVgap(10);
    grid.setHgap(10);
    init();
  }

  @Override
  public String toString() {
    return "Greedy";
  }

  private void init() {
    testECs.add(new LengthDescription());
    ProbabilityDescription probabilityT = new ProbabilityDescription();
    chosenTestEC = probabilityT;
    testECs.add(probabilityT);
    testECs.add(new LengthProbabilityDescription());
    testECs.add(new EndlessDescription());
    testECs.add(new TimeDescription());
  }

  @Override
  public Pane createPane() {
    createTimeOut();
    createThreshold();
    createMax();
    createPopulation();
    createTestECPane();
    createAlgorithmPane();
    setParameters();
    return grid;
  }

  public void createThreshold() {
    Label label = new Label("Threshold:");
    thresholdField.setPrefColumnCount(5);
    grid.add(label, 0, thresholdY);
    grid.add(thresholdField, 1, thresholdY);
  }

  public void createTimeOut() {
    Label label = new Label("Timeout:");
    timeoutField.setPrefColumnCount(5);
    timeUnitComboBox.getItems().addAll(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS);
    timeUnitComboBox.setValue(TimeUnit.SECONDS);
    grid.add(label, 0, timeoutY);
    grid.add(timeoutField, 1, timeoutY);
    grid.add(timeUnitComboBox, 2, timeoutY);
  }

  public void createMax() {
    Label label = new Label("Max tests:");
    maxTestsField.setPrefColumnCount(5);
    grid.add(label, 0, maxY);
    grid.add(maxTestsField, 1, maxY);
  }

  public void createPopulation() {
    Label label = new Label("Population:");
    populationField.setPrefColumnCount(5);
    grid.add(label, 0, populationY);
    grid.add(populationField, 1, populationY);
  }

  //TODO: this should be separate class for reuse with others
  public void createTestECPane() {
    if (oldTestBox != null) {
      grid.getChildren().remove(oldTestBox);
    } else {
      Text testECLabel = new Text("Test endcondition:");
      grid.add(testECLabel, 0, testY);

      testButton = new Button("...");
      testButton.setOnAction(event -> {
        Stage stage = new Stage();
        stage.setScene(new Scene(chosenTestEC.createEditor(this, stage)));
        stage.show();
      });
      grid.add(testButton, 2, testY);
    }

    ComboBox<ECDescription> ecBox = new ComboBox<>();
    ObservableList<ECDescription> items = ecBox.getItems();
    items.addAll(testECs);
    ecBox.setValue(chosenTestEC);
    ecBox.setOnAction(event1 -> {
      chosenTestEC = ecBox.getValue();
      testButton.setDisable(!chosenTestEC.supportsEditing());
    });
    grid.add(ecBox, 1, testY);
    oldTestBox = ecBox;
    oldTestBox.setMaxWidth(200);
  }

  @Override
  public void createSuiteECPane() {
  }

  public void createAlgorithmPane() {
    Text label = new Text("Algorithm:");
    grid.add(label, 0, algoY);
    algoBox = new ComboBox<>();
    ObservableList<AlgorithmDescription> items = algoBox.getItems();
    RandomDescription random = new RandomDescription();
    BalancingDescription balancing = new BalancingDescription();
    WeightedRandomDescription weightedRandom = new WeightedRandomDescription();
    WeightedBalancingDescription weightedBalancing = new WeightedBalancingDescription();
    items.addAll(random, balancing, weightedRandom, weightedBalancing);
    algoBox.setValue(random);
    grid.add(algoBox, 1, algoY);
  }

  public void setParameters() {
    GreedyParameters gp = state.getGreedyParameters();
    thresholdField.setText(""+gp.getThreshold());
    maxTestsField.setText(""+gp.getMaxTests());
    timeUnitComboBox.setValue(gp.getTimeUnit());
    timeoutField.setText("" + gp.getTimeout());
    populationField.setText(""+gp.getPopulation());
    //TODO: move to own class
    EndCondition endCondition = gp.getTestEndCondition();
    ObservableList<ECDescription> items = oldTestBox.getItems();
    for (ECDescription item : items) {
      if (item.getEndCondition().getClass().equals(endCondition.getClass())) {
        oldTestBox.setValue(item);
        break;
      }
    }

    FSMTraversalAlgorithm algorithm = gp.getAlgorithm();
    ObservableList<AlgorithmDescription> algoItems = algoBox.getItems();
    for (AlgorithmDescription algo : algoItems) {
      if (algo.getAlgorithm().getClass().equals(algorithm.getClass())) {
        algoBox.setValue(algo);
        break;
      }
    }
  }

  public void storeParameters() {
    int threshold = Integer.parseInt(thresholdField.getText());
    int maxTests = Integer.parseInt(maxTestsField.getText());
    int timeout = Integer.parseInt(timeoutField.getText());
    int population = Integer.parseInt(populationField.getText());
    TimeUnit timeUnit = timeUnitComboBox.getValue();
    GreedyParameters gp = state.getGreedyParameters();
    gp.setThreshold(threshold);
    gp.setMaxTests(maxTests);
    gp.setTimeout(timeout, timeUnit);
    gp.setPopulation(population);
    gp.setTestEndCondition(chosenTestEC.getEndCondition());
    gp.setAlgorithm(algoBox.getValue().getAlgorithm());
  }
}
