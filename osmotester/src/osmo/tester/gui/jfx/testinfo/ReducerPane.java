package osmo.tester.gui.jfx.testinfo;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.concurrent.TimeUnit;

/**
 * @author Teemu Kanstren
 */
public class ReducerPane extends GridPane {
  public ReducerPane() {
    setHgap(10);
    setVgap(10);
    add(new Label("Reducer"), 0, 0);

    createTimeoutCombo(1);
    createIterationCombo(2);
    createPopulationField(3);
    createParallelField();
    
    add(new Button("Reduce"), 0, 5, 3, 1);
  }

  private void createParallelField() {
    Label parallelLabel = new Label("Parallelism:");
    TextField parallelField = new TextField();
    parallelField.setPrefColumnCount(3);
    add(parallelLabel, 0, 4);
    add(parallelField, 1, 4);
  }

  private void createPopulationField(int y) {
    Label populationLabel = new Label("Population:");
    TextField populationField = new TextField();
    populationField.setPrefColumnCount(3);
    add(populationLabel, 0, y);
    add(populationField, 1, y);
  }

  private void createTimeoutCombo(int y) {
    Label labelDelay = new Label("Timeout:");
    TextField fieldDelay = new TextField();
    fieldDelay.setPrefColumnCount(3);
    add(labelDelay, 0, y);
    add(fieldDelay, 1, y);

    ComboBox<TimeUnit> unitBox = new ComboBox<>();
    ObservableList<TimeUnit> items = unitBox.getItems();
    items.addAll(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS);
    unitBox.setValue(TimeUnit.MINUTES);
    fieldDelay.setText("x");
    add(unitBox, 2, y);
  }

  private void createIterationCombo(int y) {
    Label labelDelay = new Label("Iteration:");
    TextField fieldDelay = new TextField();
    fieldDelay.setPrefColumnCount(3);
    add(labelDelay, 0, y);
    add(fieldDelay, 1, y);

    ComboBox<TimeUnit> unitBox = new ComboBox<>();
    ObservableList<TimeUnit> items = unitBox.getItems();
    items.addAll(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS);
    unitBox.setValue(TimeUnit.MINUTES);
    fieldDelay.setText("x");
    add(unitBox, 2, y);
  }
}
