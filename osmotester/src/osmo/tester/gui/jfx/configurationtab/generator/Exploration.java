package osmo.tester.gui.jfx.configurationtab.generator;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.concurrent.TimeUnit;

/**
 * @author Teemu Kanstren
 */
public class Exploration implements GeneratorDescription {
  protected final GridPane grid = new GridPane();
  private static final int depthY = 0, depthX = 0;
  private static final int timeoutY = 1, timeoutX = 0;
  private static final int printAllY = 2, printAllX = 0;
  private static final int parallelismY = 3, parallelismX = 0;
  private static final int fallbackProbabilityY = 4, fallbackProbabilityX = 0;
  private static final int minTestLenY = 5, minTestLenX = 0;
  private static final int maxTestLenY = 6, maxTestLenX = 0;
  private static final int scenarioY = 7, scenarioX = 0;
  private static final int minSuiteLenY = 0, minSuiteLenX = 2;
  private static final int maxSuiteLenY = 1, maxSuiteLenX = 2;
  private static final int minTestScoreY = 2, minTestScoreX = 2;
  private static final int minSuiteScoreY = 3, minSuiteScoreX = 2;
  private static final int testPlateauScoreY = 4, testPlateauScoreX = 2;
  private static final int testPlateauLenY = 5, testPlateauLenX = 2;
  private static final int suitePlateauY = 6, suitePlateauX = 2;

  public Exploration() {
    grid.setVgap(10);
    grid.setHgap(10);
  }

  @Override
  public String toString() {
    return "Exploration";
  }
  
  @Override
  public Node createPane() {
    createDepth();
    createMinTestLength();
    createMaxTestLength();
    createMinSuiteLength();
    createMaxSuiteLength();
    createMinTestScore();
    createMinSuiteScore();
    createTestPlateauScore();
    createTestPlateauLength();
    createSuitePlateauScore();
    createFallbackProbability();
    createPrintAll();
    createParallelism();
    createTimeOut();
    createScenario();
    return grid;
  }

  public void createDepth() {
    Label label = new Label("Depth:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, depthX, depthY);
    grid.add(field, depthX+1, depthY);
  }

  public void createMinTestLength() {
    Label label = new Label("Min. Test Length:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, minTestLenX, minTestLenY);
    grid.add(field, minTestLenX+1, minTestLenY);
  }

  public void createMaxTestLength() {
    Label label = new Label("Max. Test Length:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, maxTestLenX, maxTestLenY);
    grid.add(field, maxTestLenX+1, maxTestLenY);
  }

  public void createMinSuiteLength() {
    Label label = new Label("Min. Suite Length:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, minSuiteLenX, minSuiteLenY);
    grid.add(field, minSuiteLenX+1, minSuiteLenY);
  }

  public void createMaxSuiteLength() {
    Label label = new Label("Max. Suite Length:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, maxSuiteLenX, maxSuiteLenY);
    grid.add(field, maxSuiteLenX+1, maxSuiteLenY);
  }

  public void createMinTestScore() {
    Label label = new Label("Min. Test Score:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, minTestScoreX, minTestScoreY);
    grid.add(field, minTestScoreX+1, minTestScoreY);
  }

  public void createMinSuiteScore() {
    Label label = new Label("Min. Suite Score:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, minSuiteScoreX, minSuiteScoreY);
    grid.add(field, minSuiteScoreX+1, minSuiteScoreY);
  }

  public void createTestPlateauScore() {
    Label label = new Label("Test Plateau Score:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, testPlateauScoreX, testPlateauScoreY);
    grid.add(field, testPlateauScoreX+1, testPlateauScoreY);
  }

  public void createTestPlateauLength() {
    Label label = new Label("Test Plateau Length:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, testPlateauLenX, testPlateauLenY);
    grid.add(field, testPlateauLenX+1, testPlateauLenY);
  }

  public void createSuitePlateauScore() {
    Label label = new Label("Suite Plateau Score:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, suitePlateauX, suitePlateauY);
    grid.add(field, suitePlateauX+1, suitePlateauY);
  }

  public void createFallbackProbability() {
    Label label = new Label("End probability:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, fallbackProbabilityX, fallbackProbabilityY);
    grid.add(field, fallbackProbabilityX+1, fallbackProbabilityY);
  }

  public void createPrintAll() {
    Label label = new Label("Print All:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, printAllX, printAllY);
    grid.add(field, printAllX+1, printAllY);
  }

  public void createParallelism() {
    Label label = new Label("Parallelism:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
    grid.add(label, parallelismX, parallelismY);
    grid.add(field, parallelismX+1, parallelismY);
  }

  public void createScenario() {
    Label label = new Label("Scenario:");
    ComboBox<String> box = new ComboBox<>();
    box.getItems().addAll("Undefined");
    box.setValue("Undefined");
    grid.add(label, scenarioX, scenarioY);
    grid.add(box, scenarioX+1, scenarioY, 3, 1);
  }

  public void createTimeOut() {
    Label label = new Label("Timeout:");
    TextField field = new TextField();
    field.setPrefColumnCount(3);
//    ComboBox<TimeUnit> unit = new ComboBox<>();
//    unit.getItems().addAll(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS);
//    unit.setValue(TimeUnit.SECONDS);
    grid.add(label, timeoutX, timeoutY);
    grid.add(field, timeoutX+1, timeoutY);
//    grid.add(unit, timeoutX+2, timeoutY);
  }

}
