package osmo.tester.gui.jfx.configurationtab.generator;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import osmo.tester.explorer.ExplorationConfiguration;
import osmo.tester.gui.jfx.GUIState;

/**
 * @author Teemu Kanstren
 */
public class Exploration implements GeneratorDescription {
  private final GUIState state;
  protected final GridPane grid = new GridPane();
  private static final int depthY = 0, depthX = 0;
  private static final int timeoutY = 1, timeoutX = 0;
  private static final int parallelismY = 2, parallelismX = 0;
  private static final int fallbackProbabilityY = 3, fallbackProbabilityX = 0;
  private static final int minSuiteLenY = 0, minSuiteLenX = 2;
  private static final int maxSuiteLenY = 1, maxSuiteLenX = 2;
  private static final int minTestLenY = 2, minTestLenX = 2;
  private static final int maxTestLenY = 3, maxTestLenX = 2;
  private static final int printAllY = 2, printAllX = 0;
  private static final int minTestScoreY = 2, minTestScoreX = 2;
  private static final int minSuiteScoreY = 3, minSuiteScoreX = 2;
  private static final int testPlateauScoreY = 4, testPlateauScoreX = 2;
  private static final int testPlateauLenY = 5, testPlateauLenX = 2;
  private static final int suitePlateauY = 6, suitePlateauX = 2;
  private static final int scenarioY = 7, scenarioX = 0;

  private final TextField depthField = new TextField();
  private final TextField minTestLenField = new TextField();
  private final TextField maxTestLenField = new TextField();
  private final TextField minSuiteLenField = new TextField();
  private final TextField maxSuiteLenField = new TextField();
  private final TextField minTestScoreField = new TextField();
  private final TextField minSuiteScoreField = new TextField();
  private final TextField testPlateauScoreField = new TextField();
  private final TextField testPlateauLenField = new TextField();
  private final TextField suitePlateauScoreField = new TextField();
  private final TextField fallbackField = new TextField();
  private final TextField parallelismField = new TextField();
  private final TextField timeoutField = new TextField();

  public Exploration(GUIState state) {
    this.state = state;
    grid.setVgap(10);
    grid.setHgap(10);
    init();
  }

  private void init() {
    ExplorationConfiguration config = state.getExplorationConfig();
    depthField.setText(""+config.getDepth());
    minTestLenField.setText("" + config.getMinTestLength());
    maxTestLenField.setText(""+config.getMaxTestLength());
    minTestScoreField.setText(""+config.getMinTestScore());
    minSuiteLenField.setText("" + config.getMinSuiteLength());
    maxSuiteLenField.setText(""+ config.getMaxSuiteLength());
    minSuiteScoreField.setText(""+config.getMinSuiteScore());
    testPlateauScoreField.setText("" + config.getTestPlateauThreshold());
    testPlateauLenField.setText(""+config.getTestPlateauLength());
    suitePlateauScoreField.setText(""+config.getSuitePlateauThreshold());
    fallbackField.setText("" + config.getFallbackProbability());
    parallelismField.setText(""+config.getParallelism());
    timeoutField.setText(""+config.getTimeout());
  }

  @Override
  public String toString() {
    return "Exploration";
  }

  @Override
  public void createTestECPane() {
    
  }

  @Override
  public void createSuiteECPane() {

  }

  @Override
  public Node createPane() {
    createDepth();
    createMinTestLength();
    createMaxTestLength();
    createMinSuiteLength();
    createMaxSuiteLength();
//    createMinTestScore();
//    createMinSuiteScore();
//    createTestPlateauScore();
//    createTestPlateauLength();
//    createSuitePlateauScore();
    createFallbackProbability();
//    createPrintAll();
    createParallelism();
    createTimeOut();
//    createScenario();
    return grid;
  }

  public void createDepth() {
    Label label = new Label("Depth:");
    depthField.setPrefColumnCount(3);
    grid.add(label, depthX, depthY);
    grid.add(depthField, depthX+1, depthY);
  }

  public void createMinTestLength() {
    Label label = new Label("Min. Test Length:");
    minTestLenField.setPrefColumnCount(3);
    grid.add(label, minTestLenX, minTestLenY);
    grid.add(minTestLenField, minTestLenX+1, minTestLenY);
  }

  public void createMaxTestLength() {
    Label label = new Label("Max. Test Length:");
    maxTestLenField.setPrefColumnCount(3);
    grid.add(label, maxTestLenX, maxTestLenY);
    grid.add(maxTestLenField, maxTestLenX+1, maxTestLenY);
  }

  public void createMinSuiteLength() {
    Label label = new Label("Min. Suite Length:");
    minSuiteLenField.setPrefColumnCount(3);
    grid.add(label, minSuiteLenX, minSuiteLenY);
    grid.add(minSuiteLenField, minSuiteLenX+1, minSuiteLenY);
  }

  public void createMaxSuiteLength() {
    Label label = new Label("Max. Suite Length:");
    maxSuiteLenField.setPrefColumnCount(3);
    grid.add(label, maxSuiteLenX, maxSuiteLenY);
    grid.add(maxSuiteLenField, maxSuiteLenX+1, maxSuiteLenY);
  }

//  public void createMinTestScore() {
//    Label label = new Label("Min. Test Score:");
//    minTestScoreField.setPrefColumnCount(3);
//    grid.add(label, minTestScoreX, minTestScoreY);
//    grid.add(minTestScoreField, minTestScoreX+1, minTestScoreY);
//  }
//
//  public void createMinSuiteScore() {
//    Label label = new Label("Min. Suite Score:");
//    minSuiteScoreField.setPrefColumnCount(3);
//    grid.add(label, minSuiteScoreX, minSuiteScoreY);
//    grid.add(minSuiteScoreField, minSuiteScoreX+1, minSuiteScoreY);
//  }
//
//  public void createTestPlateauScore() {
//    Label label = new Label("Test Plateau Score:");
//    testPlateauScoreField.setPrefColumnCount(3);
//    grid.add(label, testPlateauScoreX, testPlateauScoreY);
//    grid.add(testPlateauScoreField, testPlateauScoreX+1, testPlateauScoreY);
//  }
//
//  public void createTestPlateauLength() {
//    Label label = new Label("Test Plateau Length:");
//    testPlateauLenField.setPrefColumnCount(3);
//    grid.add(label, testPlateauLenX, testPlateauLenY);
//    grid.add(testPlateauLenField, testPlateauLenX+1, testPlateauLenY);
//  }
//
//  public void createSuitePlateauScore() {
//    Label label = new Label("Suite Plateau Score:");
//    suitePlateauScoreField.setPrefColumnCount(3);
//    grid.add(label, suitePlateauX, suitePlateauY);
//    grid.add(suitePlateauScoreField, suitePlateauX+1, suitePlateauY);
//  }

  public void createFallbackProbability() {
    Label label = new Label("Fallback:");
    fallbackField.setPrefColumnCount(3);
    grid.add(label, fallbackProbabilityX, fallbackProbabilityY);
    grid.add(fallbackField, fallbackProbabilityX+1, fallbackProbabilityY);
  }

//  public void createPrintAll() {
//    Label label = new Label("Print All:");
//    TextField field = new TextField();
//    field.setPrefColumnCount(3);
//    grid.add(label, printAllX, printAllY);
//    grid.add(field, printAllX+1, printAllY);
//  }

  public void createParallelism() {
    Label label = new Label("Parallelism:");
    parallelismField.setPrefColumnCount(3);
    grid.add(label, parallelismX, parallelismY);
    grid.add(parallelismField, parallelismX+1, parallelismY);
  }

//  public void createScenario() {
//    Label label = new Label("Scenario:");
//    ComboBox<String> box = new ComboBox<>();
//    box.getItems().addAll("Undefined");
//    box.setValue("Undefined");
//    grid.add(label, scenarioX, scenarioY);
//    grid.add(box, scenarioX+1, scenarioY, 3, 1);
//  }

  public void createTimeOut() {
    Label label = new Label("Timeout:");
    timeoutField.setPrefColumnCount(3);
//    ComboBox<TimeUnit> unit = new ComboBox<>();
//    unit.getItems().addAll(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS);
//    unit.setValue(TimeUnit.SECONDS);
    grid.add(label, timeoutX, timeoutY);
    grid.add(timeoutField, timeoutX+1, timeoutY);
//    grid.add(unit, timeoutX+2, timeoutY);
  }

  public void storeParameters() {
    ExplorationConfiguration config = state.getExplorationConfig();
    double probability = Double.parseDouble(fallbackField.getText());
    config.setFallbackProbability(probability);
    config.setMaxSuiteLength(Integer.parseInt(maxSuiteLenField.getText()));
    config.setMaxTestLength(Integer.parseInt(maxTestLenField.getText()));
    config.setMinTestLength(Integer.parseInt(minTestLenField.getText()));
    config.setMinSuiteLength(Integer.parseInt(minSuiteLenField.getText()));
    config.setParallelism(Integer.parseInt(parallelismField.getText()));
    config.setDepth(Integer.parseInt(depthField.getText()));
    config.setTimeout(Integer.parseInt(timeoutField.getText()));
  }
}
