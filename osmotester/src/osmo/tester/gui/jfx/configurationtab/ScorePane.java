package osmo.tester.gui.jfx.configurationtab;

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import osmo.tester.OSMOConfiguration;
import osmo.tester.OSMOTester;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.gui.jfx.GUIState;
import osmo.tester.scripter.internal.TestLoader;
import osmo.tester.scripter.internal.TestScript;

import java.io.File;
import java.util.List;

/**
 * @author Teemu Kanstren
 */
public class ScorePane extends GridPane {
  private final GUIState state;
  private final TextField lengthWeight = new TextField();
  private final TextField variableCountWeight = new TextField();
  private final TextField valueWeight = new TextField();
  private final TextField stepWeight = new TextField();
  private final TextField stepPairWeight = new TextField();
  private final TextField requirementsWeight = new TextField();
  private final TextField stateWeight = new TextField();
  private final TextField statePairWeight = new TextField();

  public ScorePane(GUIState state) {
    this.state = state;
    setHgap(10);
    setVgap(10);
    add(new Label("Coverage Score Weights"), 0, 0, 3, 1);
    int y = 1;
    int x = 0;

    createLabeledTextField(lengthWeight, "Length:", "Test length weight", x, y++);
    createLabeledTextField(variableCountWeight, "Variables:", "Variable count weight", x, y++);
    createLabeledTextField(valueWeight, "Values:", "Default value weight", x, y++);
    createLabeledTextField(stepWeight, "Steps:", "Step weight", x, y++);
    createLabeledTextField(stepPairWeight, "Step pairs:", "Step pair weight", x, y++);
    createLabeledTextField(requirementsWeight, "Requirements:", "Requirement weight", x, y++);
    createLabeledTextField(stateWeight, "States:", "State weight", x, y++);
    createLabeledTextField(statePairWeight, "State pairs:", "State pair weight", x, y++);
    add(new Separator(Orientation.HORIZONTAL), 0, y++, 2, 1);
    add(new Label("Test Loader"), 0, y++, 2, 1);
    Button loadButton = new Button("Load Tests");
    add(loadButton, 0, y++, 2, 1);
    loadButton.setOnAction((event) -> loadTests());

    //single variables are ignored for now as they are rarely used in reality

    setWeights();
  }

  private void createLabeledTextField(TextField field, String labelText, String promptText, int x, int y) {
    Label label = new Label(labelText);
    add(label, x, y);
//    TextField field = new TextField();
    field.setPrefColumnCount(3);
    field.setPromptText(promptText);
    add(field, x + 1, y);
  }

  private void setWeights() {
    ExplorationConfiguration ec = state.getExplorationConfig();
    lengthWeight.setText("" + ec.getLengthWeight());
    variableCountWeight.setText("" + ec.getVariableCountWeight());
    valueWeight.setText("" + ec.getDefaultValueWeight());
    stepWeight.setText("" + ec.getStepWeight());
    stepPairWeight.setText("" + ec.getStepPairWeight());
    requirementsWeight.setText("" + ec.getRequirementWeight());
    stateWeight.setText("" + ec.getStateWeight());
    statePairWeight.setText("" + ec.getStatePairWeight());
  }

  public void storeWeights() {
    ExplorationConfiguration ec = state.getExplorationConfig();
    ScoreConfiguration sc = state.getScoreConfig();
    int length = Integer.parseInt(lengthWeight.getText());
    int variableCount = Integer.parseInt(variableCountWeight.getText());
    int value = Integer.parseInt(valueWeight.getText());
    int step = Integer.parseInt(stepWeight.getText());
    int stepPair = Integer.parseInt(stepPairWeight.getText());
    int requirements = Integer.parseInt(requirementsWeight.getText());
    int state = Integer.parseInt(stateWeight.getText());
    int statePair = Integer.parseInt(statePairWeight.getText());
    ec.setLengthWeight(length);
    sc.setLengthWeight(length);
    ec.setVariableCountWeight(variableCount);
    sc.setVariableCountWeight(variableCount);
    ec.setDefaultValueWeight(value);
    sc.setDefaultValueWeight(value);
    ec.setStepWeight(step);
    sc.setStepWeight(step);
    ec.setStepPairWeight(stepPair);
    sc.setStepPairWeight(stepPair);
    ec.setRequirementWeight(requirements);
    sc.setRequirementWeight(requirements);
    ec.setStateWeight(state);
    sc.setStateWeight(state);
    ec.setStatePairWeight(statePair);
    sc.setStatePairWeight(statePair);
  }

  //TODO: move the loader to own class/pane. perhaps make it a "generator" option?
  private void loadTests() {
    DirectoryChooser dirChooser = new DirectoryChooser();
    dirChooser.setTitle("Select Test Directory");
    //this should set it to current working directory
    File currentDir = new File(".");
    dirChooser.setInitialDirectory(currentDir);
    File dir = dirChooser.showDialog(state.getStage());
    //null means user cancelled
    if (dir == null) return;
    TestLoader loader = new TestLoader();
    List<TestScript> scripts = loader.loadTests(dir.getAbsolutePath());
    OSMOConfiguration config = state.getOsmoConfig();
    config.setScripts(scripts);
    OSMOTester tester = new OSMOTester();
    tester.setConfig(config);
    state.openSingleCoreExecution();
    //this seed should mean nothing as the generator is scripted but lets set it just in case eh..
    tester.generate(state.getSeed());
  }
}
