package osmo.tester.gui.jfx.configurationtab.generator;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import osmo.tester.OSMOConfiguration;
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
import osmo.tester.model.FSM;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class SingleCore implements GeneratorDescription {
  private final GUIState state;
  private ECDescription chosenTestEC = null;
  private ECDescription chosenSuiteEC = null;
  private AlgorithmDescription chosenAlgorithm = null;
  private List<ECDescription> testECs = new ArrayList<>();
  private List<ECDescription> suiteECs = new ArrayList<>();
  private List<AlgorithmDescription> algorithms = new ArrayList<>();
  private ComboBox<ECDescription> oldTestBox = null;
  private ComboBox<ECDescription> oldSuiteBox = null;
  private Button testButton;
  private Button suiteButton;
  private static final int testY = 1;
  private static final int suiteY = 2;
  private static final int algoY = 3;
  private static final int seedY = 4;
  protected final GridPane grid = new GridPane();
  private ComboBox<AlgorithmDescription> algoBox;

  public SingleCore(GUIState state) {
    this.state = state;
    init();
    grid.setVgap(10);
    grid.setHgap(10);
  }

  @Override
  public String toString() {
    return "Single Core";
  }
  
  @Override
  public Pane createPane() {
    createTestECPane();
    createSuiteECPane();
    createAlgorithmPane();
    return grid;
  }

  private void init() {
    testECs.add(new LengthDescription());
    ProbabilityDescription probabilityT = new ProbabilityDescription();
    chosenTestEC = probabilityT;
    testECs.add(probabilityT);
    testECs.add(new LengthProbabilityDescription());
    testECs.add(new EndlessDescription());
    testECs.add(new TimeDescription());

    suiteECs.add(new LengthDescription());
    ProbabilityDescription probabilityS = new ProbabilityDescription();
    chosenSuiteEC = probabilityS;
    suiteECs.add(probabilityS);
    suiteECs.add(new LengthProbabilityDescription());
    suiteECs.add(new EndlessDescription());
    suiteECs.add(new TimeDescription());

    algorithms.add(new RandomDescription());
    algorithms.add(new BalancingDescription());
    algorithms.add(new WeightedRandomDescription());
    algorithms.add(new WeightedBalancingDescription());

    readParameters();
  }
  
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

  public void createSuiteECPane() {
    if (oldSuiteBox != null) {
      grid.getChildren().remove(oldSuiteBox);
    } else {
      Text suiteECLabel = new Text("Suite endcondition:");
      grid.add(suiteECLabel, 0, suiteY);

      suiteButton = new Button("...");
      suiteButton.setOnAction(event -> {
        Stage stage = new Stage();
        stage.setScene(new Scene(chosenSuiteEC.createEditor(this, stage)));
        stage.show();
      });
      grid.add(suiteButton, 2, suiteY);
    }

    ComboBox<ECDescription> ecBox = new ComboBox<>();
    ObservableList<ECDescription> items = ecBox.getItems();
    items.addAll(suiteECs);
    ecBox.setValue(chosenSuiteEC);
    ecBox.setOnAction(event1 -> {
      chosenSuiteEC = ecBox.getValue();
      suiteButton.setDisable(!chosenSuiteEC.supportsEditing());
    });
    grid.add(ecBox, 1, suiteY);
    oldSuiteBox = ecBox;
    oldSuiteBox.setMaxWidth(200);
  }

  public void createAlgorithmPane() {
    Text label = new Text("Algorithm:");
    grid.add(label, 0, algoY);
    algoBox = new ComboBox<>();
    ObservableList<AlgorithmDescription> items = algoBox.getItems();
    items.addAll(algorithms);
    algoBox.setValue(chosenAlgorithm);
    grid.add(algoBox, 1, algoY);
  }

  public void storeParameters() {
    OSMOConfiguration config = state.getOsmoConfig();
    config.setAlgorithm(algoBox.getValue().getAlgorithm());
    config.setTestEndCondition(chosenTestEC.getEndCondition());
    config.setSuiteEndCondition(chosenSuiteEC.getEndCondition());
  }
  
  public void readParameters() {
    OSMOConfiguration config = state.getOsmoConfig();

    EndCondition endCondition = config.getTestCaseEndCondition();
    for (ECDescription item : testECs) {
      if (item.getEndCondition().getClass().equals(endCondition.getClass())) {
        chosenTestEC = item;
        break;
      }
    }

    EndCondition suiteEC = config.getSuiteEndCondition();
    for (ECDescription item : suiteECs) {
      if (item.getEndCondition().getClass().equals(suiteEC.getClass())) {
        chosenSuiteEC = item;
        break;
      }
    }

    FSMTraversalAlgorithm fake = config.cloneAlgorithm(0, null);
    for (AlgorithmDescription algorithm : algorithms) {
      if (algorithm.getAlgorithm().getClass().equals(fake.getClass())) {
        chosenAlgorithm = algorithm;
        break;
      }
    }
  }
}
