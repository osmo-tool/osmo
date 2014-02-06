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
  protected final GridPane grid = new GridPane();
  private static final int thresholdY = 0;
  private static final int timeoutY = 1;
  private static final int maxY = 2;

  public Greedy() {
    grid.setVgap(10);
    grid.setHgap(10);
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
    return grid;
  }

  public void createThreshold() {
    Label label = new Label("Threshold:");
    TextField field = new TextField();
    field.setPrefColumnCount(5);
    grid.add(label, 0, thresholdY);
    grid.add(field, 1, thresholdY);
  }

  public void createTimeOut() {
    Label label = new Label("Timeout:");
    TextField field = new TextField();
    field.setPrefColumnCount(5);
    ComboBox<TimeUnit> unit = new ComboBox<>();
    unit.getItems().addAll(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS);
    unit.setValue(TimeUnit.SECONDS);
    grid.add(label, 0, timeoutY);
    grid.add(field, 1, timeoutY);
    grid.add(unit, 2, timeoutY);
  }

  public void createMax() {
    Label label = new Label("Max tests:");
    TextField field = new TextField();
    field.setPrefColumnCount(5);
    grid.add(label, 0, maxY);
    grid.add(field, 1, maxY);
  }
}
