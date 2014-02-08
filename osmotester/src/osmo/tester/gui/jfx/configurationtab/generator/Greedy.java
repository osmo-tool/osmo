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
import osmo.tester.gui.jfx.GUIState;
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
  private final TextField populationField = new TextField();
  private final TextField maxTestsField = new TextField();
  private final TextField thresholdField = new TextField();
  private final TextField timeoutField = new TextField();
  private final ComboBox<TimeUnit> timeUnitComboBox = new ComboBox<>();

  public Greedy(GUIState state) {
    this.state = state;
    grid.setVgap(10);
    grid.setHgap(10);
    setParameters();
  }

  @Override
  public String toString() {
    return "Greedy";
  }
  
  @Override
  public Pane createPane() {
    createTimeOut();
    createThreshold();
    createMax();
    createPopulation();
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
  
  public void setParameters() {
    GreedyParameters gp = state.getGreedyParameters();
    thresholdField.setText(""+gp.getThreshold());
    maxTestsField.setText(""+gp.getMaxTests());
    timeUnitComboBox.setValue(gp.getTimeUnit());
    timeoutField.setText(""+gp.getTimeout());
    populationField.setText(""+gp.getPopulation());
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
  }
}
